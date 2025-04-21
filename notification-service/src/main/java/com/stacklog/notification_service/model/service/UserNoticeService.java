package com.stacklog.notification_service.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stacklog.notification_service.model.entities.UserNotice;
import com.stacklog.notification_service.model.repo.UserNoticeRepo;

@Service
public class UserNoticeService {
    
    @Autowired
    UserNoticeRepo userNoticeRepo;


    public List<UserNotice> getAll() {
        return userNoticeRepo.findAll();
    }

    public UserNotice getById(Long id){
        return userNoticeRepo.findById(id).orElseThrow();
    }

    public UserNotice save(UserNotice userNotice) {
        return userNoticeRepo.save(userNotice);
    }


}
