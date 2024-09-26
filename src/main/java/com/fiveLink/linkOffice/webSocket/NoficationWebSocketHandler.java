package com.fiveLink.linkOffice.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveLink.linkOffice.chat.service.ChatMemberService;
import com.fiveLink.linkOffice.chat.service.ChatMessageService;
import com.fiveLink.linkOffice.chat.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NoficationWebSocketHandler extends TextWebSocketHandler {
    // 모든 세션 관리
    private final Map<String, WebSocketSession> sessions = new HashMap<>();
    private final ChatRoomService chatRoomService;

    @Autowired
    public NoficationWebSocketHandler(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;

    }
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //값이 넘어오는 곳(수정불가)
        String payload = message.getPayload();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(payload, Map.class);
        String type = (String) jsonMap.get("type");// 본인 기능에서 알림 요청 타입명

        //-----------------알림 유형 조건 선언--------------------
        //if("본인 타입명".equals(type)){
            //본인 기능 메소드 선언(파라미터 값은 무조건 그대로 사용)
            //handleChatAlarm(jsonMap, session, type);
        //}

        if("noficationChat".equals(type)){
            System.out.println("testings");
            handleChatAlarm(jsonMap, session, type);
        }

    }

    //위에 선언한 본인 기능 알람 메소드 선언하고 안에서
    //private void handleChatAlarm(Map<String, Object> jsonMap, WebSocketSession session ,String type) throws Exception {

    //}

    //채팅 알림
    private void handleChatAlarm(Map<String, Object> jsonMap, WebSocketSession session ,String type) throws Exception {
        Object senderNoObj = jsonMap.get("chat_sender_no");
        Object currentRoomObj = jsonMap.get("chat_room_no");

        Long senderNo;
        Long currentRoom;

        if (senderNoObj instanceof String) {
            senderNo = Long.parseLong((String) senderNoObj);
        } else if (senderNoObj instanceof Integer) {
            senderNo = ((Integer) senderNoObj).longValue();
        } else {
            throw new IllegalArgumentException("타입 오류");
        }

        if (currentRoomObj instanceof String) {
            currentRoom = Long.parseLong((String) currentRoomObj);
        } else if (currentRoomObj instanceof Integer) {
            currentRoom = ((Integer) currentRoomObj).longValue();
        } else {
            throw new IllegalArgumentException("타입 오류");
        }

        List<Map<String, Object>> unreadCounts = new ArrayList<>();
        List<Long> userIdsInChatRoom = chatRoomService.findChatRoomMembers(currentRoom, senderNo);

        for (Long memberNo : userIdsInChatRoom)  {
            Map<String, Object> memberUnreadCount = new HashMap<>();
            memberUnreadCount.put("memberNo", memberNo);

            memberUnreadCount.put("chatRoomNo", currentRoom);

            unreadCounts.add(memberUnreadCount);
        }


        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("type", "chatAlarm");
                responseMap.put("data", unreadCounts);
                System.out.println("list: "+ unreadCounts);
                String unreadMessage = objectMapper.writeValueAsString(responseMap);
                s.sendMessage(new TextMessage(unreadMessage));
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
