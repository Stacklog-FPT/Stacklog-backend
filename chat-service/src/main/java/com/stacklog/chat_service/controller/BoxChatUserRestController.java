package com.stacklog.chat_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.chat_service.model.entities.BoxChatUser;
import com.stacklog.chat_service.model.service.BoxChatUserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/box-chat-user")
public class BoxChatUserRestController {
    @MessageMapping("/boxchatuser")
    @SendTo("/topic/chat-service")
    public ResponseEntity<Map<String, String>> sendMessage(Map<String, String> message){
        // System.out.println("oke");
        return ResponseEntity.ok().body(message);
    }

    @Autowired BoxChatUserService boxChatUserService;

    @GetMapping("/{boxChatId}")
    public ResponseEntity<List<BoxChatUser>> getBoxChatUserByBoxChatId(@RequestHeader("Authorization") String token, @PathVariable(name = "boxChatId") String boxChatId) {
        List<BoxChatUser> lists = boxChatUserService.getAllByBoxChatId(boxChatId, token);
        if (lists.isEmpty() || lists == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(lists);
    }

    @PostMapping("")
    public ResponseEntity<BoxChatUser> postBoxChatUser(@RequestHeader("Authorization") String token, @RequestBody BoxChatUser boxChatUser) {
        BoxChatUser newBoxChatUser = boxChatUserService.save(boxChatUser, token);
        if (newBoxChatUser == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok().body(newBoxChatUser);
    }
    
    @DeleteMapping("/{boxChatUserId}")
    public ResponseEntity<BoxChatUser> deleteBoxChatUser(@RequestHeader("Authorization") String token, @PathVariable(name = "boxChatUserId") String boxChatUserId) {
        BoxChatUser boxChatUser = boxChatUserService.delete(boxChatUserId, token);
        if (boxChatUser == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok().body(boxChatUser);
    }
    

}
