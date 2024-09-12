package com.fiveLink.linkOffice.webSocket;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveLink.linkOffice.chat.domain.ChatMessageDto;
import com.fiveLink.linkOffice.chat.service.ChatMessageService;

import java.io.IOException;
import java.util.*;


@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    // 모든 세션 관리
    private final Map<String, WebSocketSession> sessions = new HashMap<>();

    private ChatMessageService chatMessageService;

    @Autowired
    public ChatWebSocketHandler(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);

        // 메시지 저장
        chatMessageService.saveChatMessage(chatMessageDto);

        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }

}
