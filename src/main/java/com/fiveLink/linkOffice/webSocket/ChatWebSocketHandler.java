package com.fiveLink.linkOffice.webSocket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveLink.linkOffice.chat.domain.ChatMember;
import com.fiveLink.linkOffice.chat.domain.ChatMemberDto;
import com.fiveLink.linkOffice.chat.domain.ChatMessageDto;
import com.fiveLink.linkOffice.chat.domain.ChatRoomDto;
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
            System.out.println("checkcheck : "+ jsonMap);
            handleChatRoomAdd(jsonMap, session, type);
            System.out.println("checkcheck : "+ jsonMap);
        } else {
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
                    // 클라이언트로 보낼 데이터를 준비
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("chatRoomNo", chatRoomNo);
                    responseMap.put("members", members);
                    responseMap.put("currentMemberNo", currentMemberNo);
                    responseMap.put("type", type);
                    responseMap.put("names", names);

                    // JSON으로 변환
                    ObjectMapper objectMapper = new ObjectMapper();
                    String responseJson = objectMapper.writeValueAsString(responseMap);
                    System.out.println("testResponse:"+responseJson);

                    // 웹소켓 세션을 통해 클라이언트에 메시지 전송
                    session.sendMessage(new TextMessage(responseJson));
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

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("chatRoomNo", chatRoomNo);
            responseMap.put("members", members);
            responseMap.put("currentMemberNo", currentMemberNo);
            responseMap.put("type", type);
            responseMap.put("names", groupChatName);

            ObjectMapper objectMapper = new ObjectMapper();
            String responseJson = objectMapper.writeValueAsString(responseMap);

            session.sendMessage(new TextMessage(responseJson));

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
        Object currentChatRoomNoObj = jsonMap.get("currentChatRoomNo"); // Integer 또는 String일 수 있음

        Long currentChatRoomNo;
        if (currentChatRoomNoObj instanceof Integer) {
            currentChatRoomNo = ((Integer) currentChatRoomNoObj).longValue();  // Integer를 Long으로 변환
        } else if (currentChatRoomNoObj instanceof String) {
            currentChatRoomNo = Long.parseLong((String) currentChatRoomNoObj);  // String을 Long으로 변환
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
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }

}