package com.stacklog.task_service.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stacklog.task_service.model.entities.CheckItem;
import com.stacklog.task_service.model.repo.CheckItemRepo;

@Service
public class CheckItemService implements IService<CheckItem> {

    @Autowired
    CheckItemRepo checkItemRepo;

    @Override
    public List<CheckItem> getAll() {
        return checkItemRepo.findAll();
    }

    @Override
    public CheckItem getById(Long id) {
        return checkItemRepo.findById(id).orElseThrow();
    }

    @Override
    public CheckItem save(CheckItem e) {
        return checkItemRepo.save(e);
    }

    @Override
    public CheckItem remove(Long id) {
        CheckItem checkItem = checkItemRepo.findById(id).orElseThrow();
        checkItemRepo.deleteById(id);
        return checkItem;
    }
    
}
