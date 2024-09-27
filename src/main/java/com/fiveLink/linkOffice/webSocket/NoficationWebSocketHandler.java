package com.fiveLink.linkOffice.webSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveLink.linkOffice.chat.service.ChatRoomService;
import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.repository.MemberRepository;
import com.fiveLink.linkOffice.nofication.domain.NoficationDto;
import com.fiveLink.linkOffice.nofication.service.NoficationService;

@Component
public class NoficationWebSocketHandler extends TextWebSocketHandler {
    // 모든 세션 관리
    private final Map<String, WebSocketSession> sessions = new HashMap<>();
    private final ChatRoomService chatRoomService;
    private final NoficationService noficationService;
    private final MemberRepository memberRepository;

    @Autowired
    public NoficationWebSocketHandler(ChatRoomService chatRoomService, NoficationService noficationService,
    		MemberRepository memberRepository) {
        this.chatRoomService = chatRoomService;
        this.noficationService = noficationService;
        this.memberRepository = memberRepository;
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
            handleChatAlarm(jsonMap, session, type);
        } else if("noficationDocument".equals(type)) {
        	handleDocumentAlarm(jsonMap, session, type);
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
        String nofication_content = "메신저가 도착했습니다.";
        String nofication_title = "메신저";
        int nofication_type = 1;

        for (Long memberNo : userIdsInChatRoom)  {
            NoficationDto noficationDto = new NoficationDto();
            noficationDto.setNofication_content(nofication_content);// 알림내용
            noficationDto.setNofication_receive_no(memberNo);//알림 받는 사람
            noficationDto.setNofication_title(nofication_title);//알림 제목
            noficationDto.setNofication_type(nofication_type);//본인 기능 타입
            noficationDto.setMember_no(senderNo);//보내는 사람

            if(noficationService.insertAlarm(noficationDto) > 0){
                Map<String, Object> memberUnreadCount = new HashMap<>();
                memberUnreadCount.put("memberNo", memberNo);
                memberUnreadCount.put("chatRoomNo", currentRoom);
                unreadCounts.add(memberUnreadCount);
            }

        }
        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("type", "chatAlarm");
                responseMap.put("title", nofication_title);
                responseMap.put("content", nofication_content);
                responseMap.put("data", unreadCounts);
                String unreadMessage = objectMapper.writeValueAsString(responseMap);
                s.sendMessage(new TextMessage(unreadMessage));
            }
        }
    }
    // [박혜선]부서 문서함 알림
	public void handleDocumentAlarm(Map<String, Object> jsonMap, WebSocketSession session ,String type) throws Exception {
		Object memberNoObj = (String) jsonMap.get("memberNo");
		Object deptNoObj = (String) jsonMap.get("deptNo");
		
        Long memberNo;
        Long deptNo;

        if (memberNoObj instanceof String) {
        	memberNo = Long.parseLong((String) memberNoObj);
        } else if (memberNoObj instanceof Integer) {
        	memberNo = ((Integer) memberNoObj).longValue();
        } else {
            throw new IllegalArgumentException("타입 오류");
        }

        if (deptNoObj instanceof String) {
        	deptNo = Long.parseLong((String) deptNoObj);
        } else if (deptNoObj instanceof Integer) {
        	deptNo = ((Integer) deptNoObj).longValue();
        } else {
            throw new IllegalArgumentException("타입 오류");
        }
        
	  	List<Map<String, Object>> msg = new ArrayList<>();
	  	String nofication_content = "부서 문서함에 문서가 업로드 되었습니다.";
	  	String nofication_title = "문서함";
	  	int nofication_type = 2;
	  	
	  	Long status = 0L;	  	
	  	List<Member> members = memberRepository.findByDepartmentNoAndMemberStatus(deptNo, status);

	  	for(Member member : members) {
	  		NoficationDto noficationDto = new NoficationDto();
	  		if(member.getMemberNo() != memberNo) {
	  			noficationDto.setNofication_content(nofication_content);// 알림내용
	  			noficationDto.setNofication_receive_no(member.getMemberNo());//알림 받는 사람
	  			noficationDto.setNofication_title(nofication_title);//알림 제목
	  			noficationDto.setNofication_type(nofication_type);//본인 기능 타입
	  			noficationDto.setMember_no(memberNo);
	  		}          
		        if (noficationDto.getMember_no() != null) {
		            if (noficationService.insertAlarm(noficationDto) > 0) {
		                Map<String, Object> getter = new HashMap<>();
		                getter.put("memberNo", member.getMemberNo());
		                msg.add(getter);
		            }
		        } 
	  	}
	  	for (WebSocketSession s : sessions.values()) {
	  		if (s.isOpen()) {
	  			ObjectMapper objectMapper = new ObjectMapper();
	  			Map<String, Object> responseMap = new HashMap<>();
	  			responseMap.put("type", "documentAlarm");
	  			responseMap.put("title", nofication_title);
	  			responseMap.put("content", nofication_content);
	  			responseMap.put("data", msg);
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
