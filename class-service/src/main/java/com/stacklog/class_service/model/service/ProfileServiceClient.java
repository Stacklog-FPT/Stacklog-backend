package com.stacklog.class_service.model.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@FeignClient(name = "profile-service", url = "http://profileservice:2001/user", path = "")
public interface ProfileServiceClient {
    @GetMapping("/class/{classId}")
    List<Profile> getProfileByClassId(
            @RequestHeader("Authorization") String token,
            @PathVariable("classId") String classId);
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Profile {
    private String groupsId; // đổi tên khớp với JSON trả về

    public String getGroupsId() { return groupsId; }
    public void setGroupsId(String groupsId) { this.groupsId = groupsId; }
}