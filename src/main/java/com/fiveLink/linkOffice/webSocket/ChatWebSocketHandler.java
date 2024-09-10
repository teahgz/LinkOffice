package com.fiveLink.linkOffice.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveLink.linkOffice.chat.service.ChatMessageService;
import com.fiveLink.linkOffice.chat.domain.ChatMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private ChatMessageService chatMessageService;
    private Map<String, WebSocketSession> sessions = new HashMap<>();

    // 채팅방별로 세션을 관리
    private final Map<String, Set<WebSocketSession>> chatRooms = new HashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String payload = message.getPayload();
        System.out.println(payload);
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);

        ChatMessageDto dto = new ChatMessageDto();
        dto.setChat_room_no(chatMessageDto.getChat_room_no());
        dto.setChat_sender_no(chatMessageDto.getChat_sender_no());
        dto.setChat_content(chatMessageDto.getChat_content());
        chatMessageService.saveChatMessage(dto);
        //채팅방 세션 가져오기
        String chatRoomNo = String.valueOf(chatMessageDto.getChat_room_no());
        Set<WebSocketSession> sessions = chatRooms.getOrDefault(chatRoomNo, new HashSet<>());
        for(WebSocketSession webSocketSession : sessions){
            if(webSocketSession.isOpen()){
                webSocketSession.sendMessage(message);
            }
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        String chatRoomNo = (String) session.getAttributes().get("chatRoomNo");
        if(chatRoomNo != null){
            chatRooms.computeIfAbsent(chatRoomNo, k -> new HashSet<>()).add(session);
            System.out.println("connected" + chatRoomNo+ " : "+ session.getId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("disconnected : "+ session.getId());
        sessions.remove(session.getId());

    }

}

