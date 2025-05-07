package com.stacklog.class_service.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.stacklog.class_service.model.entities.Groupss;
import com.stacklog.class_service.model.repo.GroupsRepo;

public class GroupService {
    
    @Autowired
    GroupsRepo groupsRepo;

    public List<Groupss> getAllByUserId(String userId){
        return groupsRepo.findAll();
    } 

    public Groupss getById(String groupId) {
        return groupsRepo.findById(groupId).get();
    }

    public Groupss save(Groupss groupss) {
        if () {
            
        }
    }

}
