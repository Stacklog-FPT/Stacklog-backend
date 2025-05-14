package com.stacklog.chat_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.chat_service.model.entities.ChatMessage;
import com.stacklog.chat_service.model.service.ChatMessageService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/chat-message")
public class ChatMessageRestController {
    
    @MessageMapping("/chatmessage")
    @SendTo("/topic/chat-service")
    public ResponseEntity<Map<String, String>> sendMessage(Map<String, String> message){
        return ResponseEntity.ok().body(message);
    }

    @Autowired ChatMessageService chatMessageService;

    @GetMapping("")
    public ResponseEntity<List<ChatMessage>> getChatMessageByUserId(@RequestHeader("Authorization") String token) {
        List<ChatMessage> lists = chatMessageService.getAllByUserId(token);
        if (lists.isEmpty() || lists == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(lists);
    }

    @GetMapping("/{boxChatId}")
    public ResponseEntity<List<ChatMessage>> getChatMessageByBoxChatId(@RequestHeader("Authorization") String token, @PathVariable(name = "boxChatId") String boxChatId) {
        List<ChatMessage> lists = chatMessageService.getAllByBoxChatId(token, boxChatId);
        if (lists.isEmpty() || lists == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(lists);
    }

    @PostMapping("")
    public ResponseEntity<ChatMessage> postChatMessage(@RequestHeader("Authorization") String token, @RequestBody ChatMessage chatMessage) {
        ChatMessage newChatMessage = chatMessageService.save(chatMessage, token);  
        if (newChatMessage == null || newChatMessage.getChatMessageId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(newChatMessage);
    }

    @DeleteMapping("/{chatMessageId}")
    public ResponseEntity<ChatMessage> deleteChatMessage(@RequestHeader("Authorization") String token, @PathVariable String chatMessageId) {
        ChatMessage chatMessage = chatMessageService.delete(chatMessageId, token);
        if (chatMessage == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(chatMessage);
    }    
    
    

}
