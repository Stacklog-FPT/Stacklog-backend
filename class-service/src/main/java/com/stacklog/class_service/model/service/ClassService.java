package com.stacklog.class_service.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stacklog.class_service.dto.ClassesExcelDTO;
import com.stacklog.class_service.dto.ClassesExcelMapper;
import com.stacklog.class_service.model.entities.Classes;
import com.stacklog.class_service.model.entities.Groupss;
import com.stacklog.class_service.model.repo.ClassesRepo;
import com.stacklog.core_service.model.service.IService;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.excel.ExcelService;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;

import feign.FeignException;
import jakarta.transaction.Transactional;

@Service
public class ClassService implements IService<Classes> {

    private static final String NAME_SERVICE = "class-service";

    private static final String KAFKA_TOPIC_UPDATE = "class-service.classes.updated";
    private static final String KAFKA_TOPIC_CREATE = "class-service.classes.created";

    @Autowired
    private ExcelService excelService;

    @Autowired
    private ClassesExcelMapper classMapper;

    @Autowired
    private ClassesRepo classesRepo;

    @Autowired
    private GroupService groupService;

    @Autowired
    ProfileServiceClient profileServiceClient;

    @Autowired
    private KafkaProducer<Classes> kafkaClassProducer;

    RedisService<Classes> redisClassService;

    public ClassService(RedisService<Classes> redisClassService) {
        this.redisClassService = redisClassService;
    }

    @Override
    public Classes delete(String id, String token) {
        Classes classes = getById(id, token);
        if (classes == null) {
            return null;
        }
        classesRepo.delete(classes);
        return classes;
    }

    @Override
    public List<Classes> getAllByUserId(String token) {
        return null;
    }

    @Override
    public Classes getById(String id, String token) {
        // Classes classes = redisClassService.getById(id, token, NAME_SERVICE);
        Classes classes = null;
        if (classes == null) {
            classes = classesRepo.findById(id).orElseThrow();
            redisClassService.saveToRedis(classes, token, NAME_SERVICE);
        }
        return classes;
    }

    @Override
    public Classes save(Classes e, String token) {
        boolean isCreate = (e.getClassesId() == null || !classesRepo.existsById(e.getClassesId()));
        Classes newClasses = saveToDB(e, token);
        if (newClasses == null) {
            return null;
        }

        if (isCreate) {
            // kafka producer
            kafkaClassProducer.sendMessage(e, KAFKA_TOPIC_CREATE);
        } else {
            // kafa producer
            kafkaClassProducer.sendMessage(e, KAFKA_TOPIC_UPDATE);
        }

        Groupss groupss = new Groupss();
        groupss.setClasses(newClasses);
        groupss.setGroupsAvgScore(0.00);
        groupss.setGroupsMaxMember(0);
        groupss.setGroupsName("unassigned");
        groupss.setGroupsLeaderId(redisClassService.getCurrentUserId(token));
        groupss = groupService.save(groupss, token);

        redisClassService.saveToRedis(getById(newClasses.getClassesId(), token), token, NAME_SERVICE);

        return newClasses;
    }

    @Transactional
    private Classes saveToDB(Classes e, String token) {
        e.setUpdateAt(CommonFunction.getCurrentTime());
        e.setUpdateBy(redisClassService.getCurrentUserId(token));
        if (e.getClassesId() == null) {
            e.setCreatedAt(CommonFunction.getCurrentTime());
            e.setCreatedBy(redisClassService.getCurrentUserId(token));
            e.setClassesId(UUID.randomUUID().toString());
        }
        e.setLectureId(redisClassService.getCurrentUserId(token));
        return classesRepo.save(e);
    }

    public List<Classes> getAllBySemesterNUserId(String token, String semesterId) {

        String currentUserRole = redisClassService.getCurrentRoleId(token);
        List<Classes> classes = new ArrayList<>();
        switch (currentUserRole.toLowerCase()) {
            case "student":
                // classes = redisClassService.getAll(token, NAME_SERVICE);
                if (classes.isEmpty()) {
                    classes = classesRepo.findAllBySemesterIdNUserId(redisClassService.getCurrentUserId(token),
                            semesterId);
                    redisClassService.saveListToRedis(classes, token, NAME_SERVICE);
                }
                break;
            case "lecturer":
                // classes = redisClassService.getAll(token, NAME_SERVICE);
                if (classes.isEmpty()) {
                    classes = classesRepo.findAllByLectureIdAndSemesterSemesterId(
                            redisClassService.getCurrentUserId(token), semesterId);
                    redisClassService.saveListToRedis(classes, token, NAME_SERVICE);
                }
                break;
            case "admin":
                classes = classesRepo.findAll();
                break;
            default:
                break;
        }
        return classes;
    }

    public List<ClassesExcelDTO> importClasses(String file) throws Exception {
        return excelService.importExcel(file, classMapper);
    }

    public void exportClasses(List<Classes> data, String file,String token) throws Exception {
        List<ClassesExcelDTO> dtoList = new ArrayList<>();
                data.forEach(c -> {
            try {
                List<Profile> profiles = profileServiceClient.getProfileByClassId(token, c.getClassesId());

                profiles.forEach(p -> {
                    ClassesExcelDTO cedto = new ClassesExcelDTO();
                    cedto.setClassName(c.getClassesName());
                    cedto.setFullname(p.getFull_name());
                    cedto.setEmail(p.getEmail());
                    cedto.setWork_id(p.getWork_id());
                    cedto.setMemberCode(p.getEmail().split("@")[0]);

                    dtoList.add(cedto);
                });

            } catch (FeignException.NotFound e) {
                System.out.println("No profile found for classId = " + c.getClassesId());
            } catch (Exception e) {
                System.out.println("Error fetching profile for classId = " + c.getClassesId() + ": " + e.getMessage());
            }
        });
        excelService.exportExcel(dtoList, file, classMapper);
    }

}
