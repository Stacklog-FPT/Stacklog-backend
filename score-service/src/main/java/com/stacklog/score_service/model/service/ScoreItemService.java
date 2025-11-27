package com.stacklog.score_service.model.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stacklog.core_service.model.service.IService;
import com.stacklog.core_service.utils.CommonFunction;
import com.stacklog.core_service.utils.excel.ExcelService;
import com.stacklog.core_service.utils.kafka.KafkaProducer;
import com.stacklog.core_service.utils.redis.RedisService;
import com.stacklog.score_service.dto.ScoreExcelDTO;
import com.stacklog.score_service.dto.ScoreExcelMapper;
import com.stacklog.score_service.model.entities.ScoreItem;
import com.stacklog.score_service.model.repo.ScoreItemRepo;
import com.stacklog.score_service.model.entities.ScoreCategory;

import feign.FeignException;
import jakarta.transaction.Transactional;

@Service
public class ScoreItemService implements IService<ScoreItem> {

    private static final String NAME_SERVICE = "score-service";

    private static final String KAFKA_TOPIC_UPDATE = "score-service.scoreitem.updated";
    private static final String KAFKA_TOPIC_CREATE = "score-service.scoreitem.created";

    @Autowired
    private ScoreItemRepo scoreItemRepo;

    @Autowired
    private ScoreCategoryService scoreCategoryService;

    @Autowired
    private ClassServiceClient classServiceClient;

    @Autowired
    ProfileServiceClient profileServiceClient;

    @Autowired
    ExcelService excelService;

    @Autowired
    ScoreExcelMapper scoreExcelMapper;

    @Autowired
    private KafkaProducer<ScoreItem> kafkaScoreItemProducer;

    private final RedisService<ScoreItem> redisScoreItemService;

    public ScoreItemService(RedisService<ScoreItem> redisScoreItemService) {
        this.redisScoreItemService = redisScoreItemService;
    }

    @Override
    public List<ScoreItem> getAllByUserId(String token) {
        List<ScoreItem> lists = redisScoreItemService.getAll(token, NAME_SERVICE);
        if (lists == null || lists.isEmpty()) {
            String userId = redisScoreItemService.getCurrentUserId(token);
            lists = scoreItemRepo.findAllByUserId(userId);
            redisScoreItemService.saveListToRedis(lists, token, NAME_SERVICE);
        }
        return lists;
    }

    public List<ScoreItem> getAllByGroupId(String token, String groupId) {
        List<String> userIds = classServiceClient.getGroupStudent(token, groupId)
                .stream()
                .map(GroupStudent::getUserId)
                .filter(id -> id != null && !id.isBlank())
                .toList();
        if (userIds.isEmpty())
            return List.of();

        List<ScoreItem> scoreItems = scoreItemRepo.findScoreItemByUserIds(groupId, userIds);

        redisScoreItemService.saveListToRedis(scoreItems, token, "score-service");
        return scoreItems;
    }

    @Override
    public ScoreItem getById(String id, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public ScoreItem save(ScoreItem e, String token) {
        boolean isCreate = (e.getScoreItemId() == null || !scoreItemRepo.existsById(e.getScoreItemId()));
        e = saveToDB(e, token, isCreate);

        ScoreItem newScoreItem = scoreItemRepo.findById(e.getScoreItemId()).orElseThrow();

        if (isCreate) {
            kafkaScoreItemProducer.sendMessage(e, KAFKA_TOPIC_CREATE);
        } else {
            kafkaScoreItemProducer.sendMessage(e, KAFKA_TOPIC_UPDATE);
        }

        redisScoreItemService.saveToRedis(e, token, NAME_SERVICE);

        return newScoreItem;
    }

    @Transactional
    private ScoreItem saveToDB(ScoreItem e, String token, boolean isCreate) {
        LocalDateTime now = CommonFunction.getCurrentTime();
        String currentUserId = redisScoreItemService.getCurrentUserId(token);

        e.setUpdateAt(now);
        e.setUpdateBy(currentUserId);
        if (isCreate) {
            e.setCreatedAt(now);
            e.setCreatedBy(currentUserId);
            e.setScoreItemId(UUID.randomUUID().toString());
        }

        e.setScoreItemId(scoreItemRepo.save(e).getScoreItemId());
        return e;
    }

    @Override
    public ScoreItem delete(String id, String token) {
        ScoreItem scoreItem = scoreItemRepo.findById(id).orElseThrow();
        String userId = redisScoreItemService.getCurrentUserId(token);

        scoreItemRepo.deleteById(id);

        // Rebuild cache theo classId
        List<ScoreItem> lists = scoreItemRepo.findAllByUserId(userId);
        redisScoreItemService.saveListToRedis(lists, token, NAME_SERVICE);

        return scoreItem;
    }

    public void exportByClassId(String classesId, String file, String token) throws Exception {
        try {
            String[] plusHeaders = new String[0];
            plusHeaders = plusHeaders(classesId, token);
            List<ScoreExcelDTO> scoresDtos = covertToScoreExcel(classesId, token, plusHeaders);
            excelService.exportExcel(scoresDtos, file, scoreExcelMapper, plusHeaders);
        } catch (Exception e) {
            System.out.println("Error in class: " + classesId + " → " + e.getMessage());
        }

    }

    private String[] plusHeaders(String classId, String token) {
        List<ScoreCategory> list = scoreCategoryService.getAllByClassId(classId, token);
        String[] plusHeaders = new String[list.size()];
        for (int i = 0; i< list.size(); i++){
            plusHeaders[i] = list.get(i).getScoreCategoryName();
        }
        return plusHeaders;
    }

    private List<ScoreExcelDTO> covertToScoreExcel(String classesId, String token, String[] headers) {
        List<ScoreExcelDTO> dtoList = new ArrayList<>();
        try {
            List<Profile> profiles = profileServiceClient.getProfileByClassId(token, classesId);

            profiles.forEach(p -> {
                ScoreExcelDTO cedto = new ScoreExcelDTO();
                cedto.setClassName("PRN490");
                cedto.setFullname(p.getFull_name());
                cedto.setEmail(p.getEmail());
                cedto.setWork_id(p.getWork_id());
                cedto.setMemberCode(p.getEmail().split("@")[0]);
                List<ScoreItem> listScores = scoreItemRepo
                        .findAllByUserIdNClassId(p.get_id(), classesId);
                System.out.println(listScores.toString());
                Map<String, Double> scoreItemMap = new LinkedHashMap<>();
                for (String header : headers) {
                    Double value = listScores.stream()
                            .filter(item -> item.getScoreCategory().getScoreCategoryName().equals(header))
                            .map(ScoreItem::getScoreItemValue)
                            .findFirst()
                            .orElse(null);
                    scoreItemMap.put(header, value);
                }
                System.out.println(scoreItemMap.toString());
                cedto.setListScores(scoreItemMap);
                dtoList.add(cedto);
            });
        } catch (FeignException.NotFound e) {
            System.out.println("No profile found for classId = " + classesId);
        } catch (Exception e) {
            System.out
                    .println("Error fetching profile for classId = " + classesId + ": " + e.getMessage());
        }
        return dtoList;
    }

}
