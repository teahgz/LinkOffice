package com.fiveLink.linkOffice.webSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveLink.linkOffice.chat.domain.*;
import com.fiveLink.linkOffice.chat.service.ChatMemberService;
import com.fiveLink.linkOffice.chat.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fiveLink.linkOffice.chat.service.ChatMessageService;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    // 모든 세션 관리
    private final Map<String, WebSocketSession> sessions = new HashMap<>();

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final ChatMemberService chatMemberService;
    @Autowired
    public ChatWebSocketHandler(ChatMessageService chatMessageService, ChatRoomService chatRoomService, ChatMemberService chatMemberService) {
        this.chatMessageService = chatMessageService;
        this.chatRoomService = chatRoomService;
        this.chatMemberService = chatMemberService;

    }
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {


        String payload = message.getPayload();
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> jsonMap = objectMapper.readValue(payload, Map.class);
        String type = (String) jsonMap.get("type");  // 메시지 타입 확인
        if ("chatRoomCreation".equals(type)) {
            // 채팅방 생성 관련 처리
            handleChatRoomCreation(jsonMap, session, type);
        } else if("chatRoomUpdate".equals(type)){
            //채팅방 이름 수정
            handleChatRoomUpdate(jsonMap, session, type);
        } else if("chatRoomAdd".equals(type)){
            //그룹 채팅 추가
            handleChatRoomAdd(jsonMap, session, type);

        }else if("markAsRead".equals(type)){
            handleChatRead(jsonMap, session, type);
        }
        else if("getUnreadCounts".equals(type)){
            handleChatCount(jsonMap, session, type);
        }
        else {
            // 일반 채팅 메시지 처리
            ChatMessageDto chatMessageDto = objectMapper.convertValue(jsonMap, ChatMessageDto.class);
            chatMessageService.saveChatMessage(chatMessageDto);

            for (WebSocketSession s : sessions.values()) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(payload));
                }
            }
        }
    }

    // 채팅방 생성 처리 메소드
    private void handleChatRoomCreation(Map<String, Object> jsonMap, WebSocketSession session ,String type) throws Exception {

        List<String> members = (List<String>) jsonMap.get("members");
        Long currentMemberNo = Long.parseLong((String) jsonMap.get("currentMemberNo")); // 변환
        List<String> names = (List<String>) jsonMap.get("names");
        String currentMemberName = (String) jsonMap.get("currentMemberName");
        ChatRoomDto dto = new ChatRoomDto();

        if(members.size() == 1){
            //먼저 채팅방 먼저 만들기
            dto.setChat_room_type(0);//1:1 채팅방 타입
            Long chatRoomNo = chatRoomService.createRoomOne(dto);
            String position = chatRoomService.searchPosition(currentMemberNo);
            //채팅방 이름을 위한 이름+부서명
            String namePosition = currentMemberName + " " + position;

            ChatMemberDto memberDto = new ChatMemberDto();
            memberDto.setMember_no(Long.valueOf(members.get(0)));
            memberDto.setChat_room_no(chatRoomNo);
            memberDto.setChat_member_room_name(namePosition);

            if(chatMemberService.createMemberRoomOne(memberDto)>0){
                ChatMemberDto memberDto2 = new ChatMemberDto();
                memberDto2.setMember_no(currentMemberNo);
                memberDto2.setChat_room_no(chatRoomNo);
                memberDto2.setChat_member_room_name(names.get(0));

                if(chatMemberService.createMemberRoomOne(memberDto2)>0){
                    // 채팅방 번호로 해당 채팅방에 속한 멤버들 정보 조회
                    List<ChatMemberDto> chatMembers = chatMemberService.getMembersByChatRoomNo(chatRoomNo);
                    List<Map<String, Object>> memberInfoList = new ArrayList<>();

                    for(ChatMemberDto member : chatMembers){
                        Map<String, Object> memberInfo = new HashMap<>();
                        memberInfo.put("memberNo", member.getMember_no());
                        memberInfo.put("roomName", member.getChat_member_room_name());
                        memberInfoList.add(memberInfo);
                    }
                    // 클라이언트로 보낼 데이터를 준비
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("chatRoomNo", chatRoomNo);
                    responseMap.put("currentMemberNo", currentMemberNo);
                    responseMap.put("type", type);
                    responseMap.put("memberInfoList", memberInfoList);


                    // JSON으로 변환
                    ObjectMapper objectMapper = new ObjectMapper();
                    String responseJson = objectMapper.writeValueAsString(responseMap);

                    for (WebSocketSession s : sessions.values()) {
                        if (s.isOpen()) {

                            s.sendMessage(new TextMessage(responseJson));
                        }
                    }
                }
            }

        }else{
            //단체채팅방 만들기
            dto.setChat_room_type(1);//단체 채팅방 타입
            String groupChatName =(String) jsonMap.get("groupChatName");// 그룹 채팅명
            dto.setChat_room_name(groupChatName);
            Long chatRoomNo = chatRoomService.createRoomMany(dto);

            for(int i = 0; i < members.size(); i++){
                ChatMemberDto memberDto = new ChatMemberDto();
                memberDto.setChat_room_no(chatRoomNo);
                memberDto.setChat_member_room_name(groupChatName);
                memberDto.setMember_no(Long.valueOf(members.get(i)));
                // 멤버 추가
                chatMemberService.createMemberRoomMany(memberDto);

            }
            ChatMemberDto currentMemberDto = new ChatMemberDto();
            currentMemberDto.setMember_no(currentMemberNo);
            currentMemberDto.setChat_room_no(chatRoomNo);
            currentMemberDto.setChat_member_room_name(groupChatName);
            chatMemberService.createMemberRoomOne(currentMemberDto);

            if (!members.contains(currentMemberNo)) {
                members.add(String.valueOf(currentMemberNo));
            }

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("chatRoomNo", chatRoomNo);
            responseMap.put("members", members);
            responseMap.put("type", "groupChatCreate");
            responseMap.put("names", groupChatName);

            ObjectMapper objectMapper = new ObjectMapper();
            String responseJson = objectMapper.writeValueAsString(responseMap);

            for (WebSocketSession s : sessions.values()) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(responseJson));
                }
            }

        }


    }

    private void handleChatRoomUpdate(Map<String, Object> jsonMap, WebSocketSession session ,String type) throws Exception {
        String chatRoomName = (String) jsonMap.get("chatRoomName");
        Long currentMemberNo = Long.parseLong((String) jsonMap.get("currentMemberNo"));
        Integer roomNoInt = (Integer) jsonMap.get("roomNo");
        Long roomNo = roomNoInt.longValue();

        if(chatMemberService.updateChatRoom(chatRoomName, currentMemberNo, roomNo) > 0){
            Map<String, Object> response = new HashMap<>();
            response.put("updatedChatRoomName", chatRoomName);
            response.put("roomNo", roomNo);
            response.put("type", type);
            String jsonResponse = new ObjectMapper().writeValueAsString(response);
            session.sendMessage(new TextMessage(jsonResponse));
        }

    }

    // 채팅방 초대 처리 메소드
    private void handleChatRoomAdd(Map<String, Object> jsonMap, WebSocketSession session ,String type) throws Exception {
        String chatRoomName = (String) jsonMap.get("name"); // 방 이름 가져오기
        Object currentChatRoomNoObj = jsonMap.get("currentChatRoomNo");

        Long currentChatRoomNo;
        if (currentChatRoomNoObj instanceof Integer) {
            currentChatRoomNo = ((Integer) currentChatRoomNoObj).longValue();
        } else if (currentChatRoomNoObj instanceof String) {
            currentChatRoomNo = Long.parseLong((String) currentChatRoomNoObj);
        } else {
            throw new IllegalArgumentException("채팅방 번호의 오류");
        }

        List<String> members = (List<String>) jsonMap.get("newMembers");

        ChatMemberDto memberDto = new ChatMemberDto();
        for(int i = 0; i< members.size(); i++){
            memberDto.setMember_no(Long.valueOf(members.get(i))); //memberNo
            memberDto.setChat_room_no(currentChatRoomNo);
            memberDto.setChat_member_room_name(chatRoomName);
            if(chatMemberService.createMemberRoomOne(memberDto)>0){

                // 새 멤버 정보를 클라이언트에 전송
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("type", "memberAdded");
                responseMap.put("chatRoomNo", currentChatRoomNo);
                responseMap.put("chatRoomName", chatRoomName);
                responseMap.put("member", Long.valueOf(members.get(i)));

                for (WebSocketSession s : sessions.values()) {
                    if (s.isOpen()) {
                        String responseJson = new ObjectMapper().writeValueAsString(responseMap);
                        s.sendMessage(new TextMessage(responseJson));
                    }
                }
            }
        }

    }
    //채팅 읽음 처리
    private void handleChatRead(Map<String, Object> jsonMap, WebSocketSession session ,String type) throws Exception {
        Long currentMemberNo = Long.parseLong((String) jsonMap.get("currentMember"));
        Long roomNo = ((Number) jsonMap.get("chatRoomNo")).longValue();
        List<Long> messageNo = chatMessageService.markMessagesAsReadForChatRoom(currentMemberNo, roomNo);
        if (!messageNo.isEmpty()) {
            for (Long i : messageNo) {
                ChatReadDto chatReadDto = new ChatReadDto();
                chatReadDto.setMember_no(currentMemberNo);
                chatReadDto.setChat_message_no(i);

                chatMessageService.insertReadStatus(chatReadDto);
           }

        }
        Map<String, Object> response = new HashMap<>();
        response.put("type", "updateUnreadCount");
        response.put("chatRoomNo", roomNo);
       response.put("unreadCount", 0);

        session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(response)));
    }

    //채팅 읽음 개수
    private void handleChatCount(Map<String, Object> jsonMap, WebSocketSession session ,String type) throws Exception {
        Long currentMemberNo = Long.parseLong((String) jsonMap.get("currentMember"));

        List<Map<String, Object>> unreadCounts = chatMessageService.getUnreadCounts(currentMemberNo);
        Map<String, Object> response = new HashMap<>();
        response.put("type", "unreadCounts");
        // 읽지 않은 메시지 개수 추가
        List<Map<String, Object>> unreadResponseList = new ArrayList<>();
        for (Map<String, Object> count : unreadCounts) {
            Map<String, Object> unreadResponse = new HashMap<>();
            unreadResponse.put("chatRoomNo", count.get("chat_room_no"));
            unreadResponse.put("unreadCount", count.get("unread_count"));
            unreadResponseList.add(unreadResponse);
        }

        response.put("data", unreadResponseList);
        session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(response)));
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