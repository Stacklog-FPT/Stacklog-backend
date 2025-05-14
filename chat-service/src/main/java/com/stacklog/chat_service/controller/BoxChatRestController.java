package com.stacklog.chat_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.chat_service.model.entities.BoxChat;
import com.stacklog.chat_service.model.service.BoxChatService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/box-chat")
public class BoxChatRestController {

    @MessageMapping("/boxchat")
    @SendTo("/topic/chat-service")
    public ResponseEntity<Map<String, String>> sendMessage(Map<String, String> message){
        // System.out.println("oke");
        return ResponseEntity.ok().body(message);
    }
    
    @Autowired BoxChatService boxChatService;

    @GetMapping("")
    public ResponseEntity<List<BoxChat>> getBoxChatByUserId(@RequestHeader("Authorization") String token) {
        List<BoxChat> lists = boxChatService.getAllByUserId(token);
        if (lists.isEmpty() || lists == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(lists);
    }

    @PostMapping("")
    public ResponseEntity<BoxChat> postBoxChat(@RequestHeader("Authorization") String token, @RequestBody BoxChat boxChat) {
        BoxChat newBoxChat = boxChatService.save(boxChat, token);
        if (newBoxChat == null || newBoxChat.getBoxChatId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(newBoxChat);
    }

    @DeleteMapping("/{boxChatId}")
    public ResponseEntity<BoxChat> deleteBoxChat(@RequestHeader("Authorization") String token, @PathVariable String boxChatId) {
        BoxChat boxChat = boxChatService.delete(boxChatId, token);
        if (boxChat == null || boxChat.getBoxChatId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(boxChat);
    }
    
    

}
