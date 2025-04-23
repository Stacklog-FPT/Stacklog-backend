package com.stacklog.task_service.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stacklog.task_service.model.entities.CheckList;
import com.stacklog.task_service.model.repo.CheckListRepo;

@Service
public class CheckListService implements IService<CheckList> {

    @Autowired
    CheckListRepo checkListRepo;

    @Override
    public List<CheckList> getAll() {
        return checkListRepo.findAll();
    }

    @Override
    public CheckList getById(String id) {
        return checkListRepo.findById(id).orElseThrow();
    }

    @Override
    public CheckList save(CheckList e) {
        return checkListRepo.save(e);
    }

    @Override
    public CheckList remove(String id) {
        CheckList checkList = getById(id);
        if (checkList == null) {
            return null;
        }
        checkListRepo.deleteById(id);
        ;
        return checkList;
    }

    public List<CheckList> getCheckListsByIdTask(Long taskId) {
        return getAll().stream()
                .filter(checkList -> checkList.getTask().getTaskId().equals(taskId))
                .toList();
    }

}
