package com.stacklog.chat_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stacklog.chat_service.model.entities.*;
import com.stacklog.chat_service.model.service.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class ChatServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoxChatService boxChatService;

    @MockBean
    private BoxChatUserService boxChatUserService;

    @MockBean
    private ChatMessageService chatMessageService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String dummyToken = "Bearer test-token";

    // ============================ BOX CHAT ============================

    @Test
    void testGetBoxChatsByUserId() throws Exception {
        when(boxChatService.getAllByUserId(dummyToken))
                .thenReturn(Collections.singletonList(new BoxChat()));

        mockMvc.perform(get("/box-chat")
                .header("Authorization", dummyToken))
                .andExpect(status().isOk());
    }

    @Test
    void testPostBoxChat() throws Exception {
        BoxChat mockChat = new BoxChat();
        mockChat.setBoxChatId(UUID.randomUUID().toString());

        when(boxChatService.save(mockChat, dummyToken)).thenReturn(mockChat);

        mockMvc.perform(post("/box-chat")
                .header("Authorization", dummyToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockChat)))
                .andExpect(status().isOk());
    }

    // ============================ BOX CHAT USER ============================

    @Test
    void testGetBoxChatUserByBoxChatId() throws Exception {
        when(boxChatUserService.getAllByBoxChatId("chat-1", dummyToken))
                .thenReturn(Collections.singletonList(new BoxChatUser()));

        mockMvc.perform(get("/box-chat-user/chat-1")
                .header("Authorization", dummyToken))
                .andExpect(status().isOk());
    }

    @Test
    void testPostBoxChatUser() throws Exception {
        BoxChatUser user = new BoxChatUser();
        user.setBoxChatUserId("user-1");

        when(boxChatUserService.save(user, dummyToken)).thenReturn(user);

        mockMvc.perform(post("/box-chat-user")
                .header("Authorization", dummyToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    // ============================ CHAT MESSAGE ============================

    @Test
    void testGetChatMessagesByBoxChatId() throws Exception {
        when(chatMessageService.getAllByBoxChatId(dummyToken, "chat-1"))
                .thenReturn(Collections.singletonList(new ChatMessage()));

        mockMvc.perform(get("/chat-message/chat-1")
                .header("Authorization", dummyToken))
                .andExpect(status().isOk());
    }

    @Test
    void testPostChatMessage() throws Exception {
        ChatMessage msg = new ChatMessage();
        msg.setChatMessageId("msg-1");

        when(chatMessageService.save(msg, dummyToken)).thenReturn(msg);

        mockMvc.perform(post("/chat-message")
                .header("Authorization", dummyToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(msg)))
                .andExpect(status().isOk());
    }
}
