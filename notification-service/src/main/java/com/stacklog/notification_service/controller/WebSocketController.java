package com.stacklog.notification_service.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

// import com.stacklog.notification_service.model.entities.Notification;

@RestController
public class WebSocketController {

    @MessageMapping("/notify")
    @SendTo("/topic/notification")
    public ResponseEntity<Map<String, String>> sendMessage(Map<String, String> message){
        System.out.println("oke");
        return ResponseEntity.ok().body(message);
    }

}
