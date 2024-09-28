package com.fiveLink.linkOffice.webSocket;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveLink.linkOffice.approval.domain.ApprovalDto;
import com.fiveLink.linkOffice.approval.domain.ApprovalFlowDto;
import com.fiveLink.linkOffice.approval.service.ApprovalService;
import com.fiveLink.linkOffice.chat.service.ChatRoomService;
import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.repository.MemberRepository;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.nofication.domain.NoficationDto;
import com.fiveLink.linkOffice.nofication.service.NoficationService;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalDto;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFlowDto;
import com.fiveLink.linkOffice.vacationapproval.service.VacationApprovalService;

@Component
public class NoficationWebSocketHandler extends TextWebSocketHandler {
    // 모든 세션 관리
    private final Map<String, WebSocketSession> sessions = new HashMap<>();
    private final ChatRoomService chatRoomService;
    private final NoficationService noficationService;
    private final MemberRepository memberRepository;
    private final ApprovalService approvalService;
    private final VacationApprovalService vacationApprovalService;
    private final MemberService memberService;

    @Autowired
    public NoficationWebSocketHandler(ChatRoomService chatRoomService, NoficationService noficationService,
    		MemberRepository memberRepository,
    		ApprovalService approvalService,
    		VacationApprovalService vacationApprovalService,
    		MemberService memberService) {
        this.chatRoomService = chatRoomService;
        this.noficationService = noficationService;
        this.memberRepository = memberRepository;
        this.approvalService = approvalService;
        this.vacationApprovalService = vacationApprovalService;
        this.memberService = memberService;
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
        } else if("notificationVacationApproval".equals(type)) {
        	handleVacationApprovalAlarm(jsonMap, session, type);
        }  else if("notificationVacationApprovalReviewers".equals(type)) {
        	handleVacationAppReviewersAlarm(jsonMap, session, type);
        } else if("notificationVacationAppApprove".equals(type)) {
        	handleVacationAppApproveAlarm(jsonMap, session, type);
        } else if("notificationVacationAppReject".equals(type)) {
        	handleVacationAppRejectAlarm(jsonMap, session, type);
        } else if("notificationApproval".equals(type)) {
        	handleApprovalAlarm(jsonMap, session, type);
        } else if("notificationApprovalReviewers".equals(type)) {
        	handleApprovalReviewersAlarm(jsonMap, session, type);
        } else if("notificationAppApprove".equals(type)) {
        	handleAppApproveAlarm(jsonMap, session, type);
        } else if("notificationAppReject".equals(type)) {
        	handleAppRejectAlarm(jsonMap, session, type);
        }

    }

    //위에 선언한 본인 기능 알람 메소드 선언하고 안에서
    //private void handleChatAlarm(Map<String, Object> jsonMap, WebSocketSession session ,String type) throws Exception {

    //}
	//실시간 시간
	public String getCurrentFormattedDateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yy.MM.dd a hh:mm");
		Date date = new Date();
		return formatter.format(date);
	}
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

			long notificationPk = noficationService.insertAlarmPk();

            if(noficationService.insertAlarm(noficationDto) > 0){
                Map<String, Object> memberUnreadCount = new HashMap<>();
                memberUnreadCount.put("memberNo", memberNo);
                memberUnreadCount.put("chatRoomNo", currentRoom);
				memberUnreadCount.put("nofication_pk", notificationPk);
                unreadCounts.add(memberUnreadCount);
            }

        }
        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("type", "chatAlarm");
				responseMap.put("nofication_type", nofication_type);
                responseMap.put("title", nofication_title);
                responseMap.put("content", nofication_content);
                responseMap.put("data", unreadCounts);
				String currentTime = getCurrentFormattedDateTime();
				responseMap.put("timestamp", currentTime);
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
			long notificationPk = noficationService.insertAlarmPk();
		        if (noficationDto.getMember_no() != null) {
		            if (noficationService.insertAlarm(noficationDto) > 0) {
		                Map<String, Object> getter = new HashMap<>();
		                getter.put("memberNo", member.getMemberNo());
						getter.put("nofication_pk", notificationPk);
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
				String currentTime = getCurrentFormattedDateTime();
				responseMap.put("timestamp", currentTime);
	  			String unreadMessage = objectMapper.writeValueAsString(responseMap);
	  			s.sendMessage(new TextMessage(unreadMessage));
	  		}
	  	}
	 }   
    
	// [전주영] 휴가결재 등록 알림
	private void handleVacationApprovalAlarm(Map<String, Object> jsonMap, WebSocketSession session, String type) throws Exception {
		Map<String, Object> notificationData = (Map<String, Object>) jsonMap.get("notificationData");
		// 기안자
		Object sendNoobj = jsonMap.get("memberNo");
		Long senderNo = null;
		
		if (sendNoobj instanceof String) {
			senderNo = Long.parseLong((String) sendNoobj);
		} else if (sendNoobj instanceof Integer) {
			senderNo = ((Integer) sendNoobj).longValue();
		} else {
		    throw new IllegalArgumentException("타입 오류");
		}

		List<Long> approvers = null;
		if (notificationData.get("approvers") instanceof List) {
		    approvers = ((List<?>) notificationData.get("approvers")).stream()
		        .map(value -> {
		            if (value instanceof String) {
		                return Long.parseLong((String) value);
		            } else if (value instanceof Integer) {
		                return ((Integer) value).longValue(); 
		            } else if (value instanceof Long) {
		                return (Long) value; 
		            } else {
		                throw new IllegalArgumentException("타입 오류");
		            }
		        })
		        .collect(Collectors.toList());
		}

		List<Long> references = null;
		if (notificationData.get("references") instanceof List) {
		    references = ((List<?>) notificationData.get("references")).stream()
		        .map(value -> {
		            if (value instanceof String) {
		                return Long.parseLong((String) value);
		            } else if (value instanceof Integer) {
		                return ((Integer) value).longValue();
		            } else if (value instanceof Long) {
		                return (Long) value;
		            } else {
		                throw new IllegalArgumentException("타입 오류");
		            }
		        })
		        .collect(Collectors.toList());
		}

		List<Long> reviewers = null;
		if (notificationData.get("reviewers") instanceof List) {
		    reviewers = ((List<?>) notificationData.get("reviewers")).stream()
		        .map(value -> {
		            if (value instanceof String) {
		                return Long.parseLong((String) value);
		            } else if (value instanceof Integer) {
		                return ((Integer) value).longValue();
		            } else if (value instanceof Long) {
		                return (Long) value;
		            } else {
		                throw new IllegalArgumentException("알 수 없는 타입: " + value.getClass().getName());
		            }
		        })
		        .collect(Collectors.toList());
		}
		
		MemberDto member = memberService.selectMemberOne(senderNo);
		
		 String nofication_content = member.getMember_name()+"님이 올린 결재 문서의 차례가 되었습니다.";
	     String nofication_title = "휴가결재"; 
	     int nofication_type = 3;
	     
	    NoficationDto noficationDto = new NoficationDto();
	    
	    List<Map<String, Object>> unreadCounts = new ArrayList<>();
	    
	 // References가 있을 경우, 첫 번째 reference에게만 알림 전송
	    if (references != null && !references.isEmpty()) {
	        Long firstReference = references.get(0); 
	        noficationDto.setNofication_content(nofication_content);
	        noficationDto.setNofication_receive_no(firstReference);
	        noficationDto.setNofication_title(nofication_title);
	        noficationDto.setNofication_type(nofication_type);
	        noficationDto.setMember_no(senderNo);
	        
	        if (noficationService.insertAlarm(noficationDto) > 0) {
	            Map<String, Object> memberUnreadCount = new HashMap<>();
	            memberUnreadCount.put("memberNo", firstReference);
	            unreadCounts.add(memberUnreadCount);
	        }
	    } else if (approvers != null && !approvers.isEmpty()) {
	        // Reference가 없을 경우, 첫 번째 approver에게 알림 전송
	        Long firstApprover = approvers.get(0); 
	        noficationDto.setNofication_content(nofication_content);
	        noficationDto.setNofication_receive_no(firstApprover);
	        noficationDto.setNofication_title(nofication_title);
	        noficationDto.setNofication_type(nofication_type);
	        noficationDto.setMember_no(senderNo);
	        
	        if (noficationService.insertAlarm(noficationDto) > 0) {
	            Map<String, Object> memberUnreadCount = new HashMap<>();
	            memberUnreadCount.put("memberNo", firstApprover);
	            unreadCounts.add(memberUnreadCount);
	        }
	    }

	    // Reviewers 처리: 모든 reviewer에게 알림 전송
	    if (reviewers != null) {
	        for (Long reviewer : reviewers) {
	            noficationDto.setNofication_content(nofication_content);
	            noficationDto.setNofication_receive_no(reviewer);
	            noficationDto.setNofication_title(nofication_title);
	            noficationDto.setNofication_type(nofication_type);
	            noficationDto.setMember_no(senderNo);
				long notificationPk = noficationService.insertAlarmPk(); // pk값 추가
	            if (noficationService.insertAlarm(noficationDto) > 0) {
	                Map<String, Object> memberUnreadCount = new HashMap<>();
	                memberUnreadCount.put("memberNo", reviewer);
					memberUnreadCount.put("nofication_pk", notificationPk);//같이 넘길 값
	                unreadCounts.add(memberUnreadCount);
	            }
	        }
	    }
	    
	    for (WebSocketSession s : sessions.values()) {
	    	if (s.isOpen()) {
	    		ObjectMapper objectMapper = new ObjectMapper();
	    		Map<String, Object> responseMap = new HashMap<>();
	    		responseMap.put("type", "vacationApprovalAlarm");
	    		responseMap.put("title", nofication_title);
	    		responseMap.put("content",nofication_content);
	    		responseMap.put("data", unreadCounts);
				String currentTime = getCurrentFormattedDateTime();
				responseMap.put("timestamp", currentTime);
	    		String unreadMessage = objectMapper.writeValueAsString(responseMap);
	    		s.sendMessage(new TextMessage(unreadMessage));
	    	}
	    }
	}
	
	// [전주영] 휴가결재 등록 알림 (참조자)
	private void handleVacationAppReviewersAlarm(Map<String, Object> jsonMap, WebSocketSession session, String type) throws Exception {
		Object sendNoobj = jsonMap.get("memberNo");

		Long senderNo = null;

		if (sendNoobj instanceof String) {
			senderNo = Long.parseLong((String) sendNoobj);
		} else if (sendNoobj instanceof Integer) {
			senderNo = ((Integer) sendNoobj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}		
		
	    List<Long> reviewers = null;

	    if (jsonMap.get("reviewers") instanceof List) {
	        reviewers = ((List<?>) jsonMap.get("reviewers")).stream().map(value -> {
	            if (value instanceof String) {
	                return Long.parseLong((String) value);  
	            } else if (value instanceof Integer) {
	                return ((Integer) value).longValue(); 
	            } else if (value instanceof Long) {
	                return (Long) value;
	            } else {
	                throw new IllegalArgumentException("타입 오류");
	            }
	        }).collect(Collectors.toList());
		}
	    	
		MemberDto member = memberService.selectMemberOne(senderNo);

		String nofication_content = member.getMember_name()+"님이 기안한 결재 문서의 참조자로 지정 되었습니다.";
	     String nofication_title = "휴가결재"; 
	     int nofication_type = 4;
	     
	    NoficationDto noficationDto = new NoficationDto();
	    
	    List<Map<String, Object>> unreadCounts = new ArrayList<>();
	    
		if (reviewers != null) {
			for (Long reviewer : reviewers) {
				noficationDto.setNofication_content(nofication_content);
				noficationDto.setNofication_receive_no(reviewer);
				noficationDto.setNofication_title(nofication_title);
				noficationDto.setNofication_type(nofication_type);
				noficationDto.setMember_no(senderNo);
				long notificationPk = noficationService.insertAlarmPk(); // pk값 추가
				if (noficationService.insertAlarm(noficationDto) > 0) {
					Map<String, Object> memberUnreadCount = new HashMap<>();
					memberUnreadCount.put("memberNo", reviewer);
					memberUnreadCount.put("nofication_pk", notificationPk);//같이 넘길 값
					unreadCounts.add(memberUnreadCount);
				}
			}
		}

		for (WebSocketSession s : sessions.values()) {
			if (s.isOpen()) {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("type", "vacationApprovalReviewsAlarm");
				responseMap.put("title", nofication_title);
				responseMap.put("content", noficationDto.getNofication_content());
				responseMap.put("data", unreadCounts);
				String currentTime = getCurrentFormattedDateTime();
				responseMap.put("timestamp", currentTime);
				String unreadMessage = objectMapper.writeValueAsString(responseMap);
				s.sendMessage(new TextMessage(unreadMessage));
			}
		}	    	    
	}
	
	// [전주영] 휴가결재 승인 알림
	private void handleVacationAppApproveAlarm(Map<String, Object> jsonMap, WebSocketSession session, String type)
			throws Exception {

		// 결재자
		Object sendNoObj = jsonMap.get("memberNo");
		// 기안자
		Object vaAppMemberObj = jsonMap.get("vaAppprovalMemberNo");
		// 문서 번호
		Object vaAppNoObj = jsonMap.get("vacationapprovalNo");

		Long senderNo;
		Long vaAppMemberNo;
		Long vaAppNo;

		if (sendNoObj instanceof String) {
			senderNo = Long.parseLong((String) sendNoObj);
		} else if (sendNoObj instanceof Integer) {
			senderNo = ((Integer) sendNoObj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}

		if (vaAppMemberObj instanceof String) {
			vaAppMemberNo = Long.parseLong((String) vaAppMemberObj);
		} else if (vaAppMemberObj instanceof Integer) {
			vaAppMemberNo = ((Integer) vaAppMemberObj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}

		if (vaAppNoObj instanceof String) {
			vaAppNo = Long.parseLong((String) vaAppNoObj);
		} else if (vaAppNoObj instanceof Integer) {
			vaAppNo = ((Integer) vaAppNoObj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}
		
		MemberDto member = memberService.selectMemberOne(vaAppMemberNo);
		
		VacationApprovalDto vacationApproval = vacationApprovalService.selectVacationApprovalOne(vaAppNo);
		
		List<VacationApprovalFlowDto> approvalDtos = vacationApprovalService.getVacationApprovalFlows(vaAppNo);
		Long nextApproverNo = findNextVacationApproverMemberNo(approvalDtos, senderNo);

		String nofication_content = member.getMember_name()+"님이 올린 결재 문서의 차례가 되었습니다.";
		String nofication_title = "휴가결재";
		int nofication_type = 5;
		
		NoficationDto noficationDto = new NoficationDto();

		List<Map<String, Object>> unreadCounts = new ArrayList<>();

		if (nextApproverNo != null) {
	         noficationDto.setNofication_content(nofication_content);
	         noficationDto.setNofication_receive_no(nextApproverNo);
	         noficationDto.setNofication_title(nofication_title);
	         noficationDto.setNofication_type(nofication_type);
	         noficationDto.setMember_no(senderNo);
			long notificationPk = noficationService.insertAlarmPk(); // pk값 추가

	         if (noficationService.insertAlarm(noficationDto) > 0) {
	            Map<String, Object> memberUnreadCount = new HashMap<>();
	            memberUnreadCount.put("memberNo", nextApproverNo);
				memberUnreadCount.put("nofication_pk", notificationPk);//같이 넘길 값
	            unreadCounts.add(memberUnreadCount);
	         }

		} else {
	         noficationDto.setNofication_content('"'+vacationApproval.getVacation_approval_title()+'"'+" 문서의 결재가 완료되었습니다.");
	         noficationDto.setNofication_receive_no(vaAppMemberNo);
	         noficationDto.setNofication_title(nofication_title);
	         noficationDto.setNofication_type(nofication_type);
	         noficationDto.setMember_no(senderNo);
			long notificationPk = noficationService.insertAlarmPk(); // pk값 추가

	         if (noficationService.insertAlarm(noficationDto) > 0) {
	            Map<String, Object> memberUnreadCount = new HashMap<>();
	            memberUnreadCount.put("memberNo", vaAppMemberNo);
				 memberUnreadCount.put("nofication_pk", notificationPk);//같이 넘길 값
	            unreadCounts.add(memberUnreadCount);
	         }
		}
		
	      for (WebSocketSession s : sessions.values()) {
	          if (s.isOpen()) {
	             ObjectMapper objectMapper = new ObjectMapper();
	             Map<String, Object> responseMap = new HashMap<>();
	             responseMap.put("type", "vacationAppApproveAlarm");
	             responseMap.put("title", nofication_title);
	             responseMap.put("content", noficationDto.getNofication_content());
	             responseMap.put("data", unreadCounts);
				  String currentTime = getCurrentFormattedDateTime();
				  responseMap.put("timestamp", currentTime);
	             String unreadMessage = objectMapper.writeValueAsString(responseMap);
	             s.sendMessage(new TextMessage(unreadMessage));
	          }
	       }
	}	

	// 다음 결재자 
	public Long findNextVacationApproverMemberNo(List<VacationApprovalFlowDto> approvalDtos, Long senderNo) {
		for (int i = 0; i < approvalDtos.size(); i++) {
			VacationApprovalFlowDto currentApproval = approvalDtos.get(i);

			if (currentApproval.getMember_no().equals(senderNo)) {
				for (int j = i + 1; j < approvalDtos.size(); j++) {
					VacationApprovalFlowDto nextApproval = approvalDtos.get(j);
					if (nextApproval.getVacation_approval_flow_order() != null) {
						return nextApproval.getMember_no();
					}
				}
				return null;
			}
		}
		return null;
	}	

	// [전주영] 휴가결재 반려 알림
	private void handleVacationAppRejectAlarm(Map<String, Object> jsonMap, WebSocketSession session, String type)
			throws Exception {

		// 결재자
		Object sendNoObj = jsonMap.get("memberNo");
		// 기안자
		Object vaAppMemberObj = jsonMap.get("vaAppprovalMemberNo");
		// 문서 번호
		Object vaAppNoObj = jsonMap.get("vacationapprovalNo");

		Long senderNo;
		Long vaAppprovalMemberNo;
		Long vaAppNo;

		if (sendNoObj instanceof String) {
			senderNo = Long.parseLong((String) sendNoObj);
		} else if (sendNoObj instanceof Integer) {
			senderNo = ((Integer) sendNoObj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}

		if (vaAppMemberObj instanceof String) {
			vaAppprovalMemberNo = Long.parseLong((String) vaAppMemberObj);
		} else if (vaAppMemberObj instanceof Integer) {
			vaAppprovalMemberNo = ((Integer) vaAppMemberObj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}

		if (vaAppNoObj instanceof String) {
			vaAppNo = Long.parseLong((String) vaAppNoObj);
		} else if (vaAppNoObj instanceof Integer) {
			vaAppNo = ((Integer) vaAppNoObj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}
		
		VacationApprovalDto vacationApproval = vacationApprovalService.selectVacationApprovalOne(vaAppNo);
		
		String nofication_content = '"'+vacationApproval.getVacation_approval_title()+'"'+" 문서의 결재가 반려되었습니다.";
		String nofication_title = "휴가결재";
		int nofication_type = 6;

		NoficationDto noficationDto = new NoficationDto();

		List<Map<String, Object>> unreadCounts = new ArrayList<>();

		noficationDto.setNofication_content(nofication_content);
		noficationDto.setNofication_receive_no(vaAppprovalMemberNo);
		noficationDto.setNofication_title(nofication_title);
		noficationDto.setNofication_type(nofication_type);
		noficationDto.setMember_no(senderNo);
		long notificationPk = noficationService.insertAlarmPk(); // pk값 추가

		if (noficationService.insertAlarm(noficationDto) > 0) {
			Map<String, Object> memberUnreadCount = new HashMap<>();
			memberUnreadCount.put("memberNo", vaAppprovalMemberNo);
			memberUnreadCount.put("nofication_pk", notificationPk);//같이 넘길 값
			unreadCounts.add(memberUnreadCount);
		}

		for (WebSocketSession s : sessions.values()) {
			if (s.isOpen()) {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("type", "appRejectAlarm");
				responseMap.put("title", nofication_title);
				responseMap.put("content", noficationDto.getNofication_content());
				responseMap.put("data", unreadCounts);
				String currentTime = getCurrentFormattedDateTime();
				responseMap.put("timestamp", currentTime);
				String unreadMessage = objectMapper.writeValueAsString(responseMap);
				s.sendMessage(new TextMessage(unreadMessage));
			}
		}

	}
		
	// [전주영] 전자결재 등록 알림
	private void handleApprovalAlarm(Map<String, Object> jsonMap, WebSocketSession session, String type)
			throws Exception {
		Map<String, Object> notificationData = (Map<String, Object>) jsonMap.get("notificationData");

		Object sendNoobj = jsonMap.get("memberNo");

		Long senderNo = null;

		if (sendNoobj instanceof String) {
			senderNo = Long.parseLong((String) sendNoobj);
		} else if (sendNoobj instanceof Integer) {
			senderNo = ((Integer) sendNoobj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}

		List<Long> approvers = null;
		if (notificationData.get("approvers") instanceof List) {
			approvers = ((List<?>) notificationData.get("approvers")).stream().map(value -> {
				if (value instanceof String) {
					return Long.parseLong((String) value);
				} else if (value instanceof Integer) {
					return ((Integer) value).longValue();
				} else if (value instanceof Long) {
					return (Long) value;
				} else {
					throw new IllegalArgumentException("타입 오류");
				}
			}).collect(Collectors.toList());
		}

		List<Long> references = null;
		if (notificationData.get("references") instanceof List) {
			references = ((List<?>) notificationData.get("references")).stream().map(value -> {
				if (value instanceof String) {
					return Long.parseLong((String) value);
				} else if (value instanceof Integer) {
					return ((Integer) value).longValue();
				} else if (value instanceof Long) {
					return (Long) value;
				} else {
					throw new IllegalArgumentException("타입 오류");
				}
			}).collect(Collectors.toList());
		}
		
		MemberDto member = memberService.selectMemberOne(senderNo);

		String nofication_content = member.getMember_name()+"님이 기안한 결재 문서의 차례가 되었습니다.";
		
		String nofication_title = "전자결재";
		int nofication_type = 7;

		NoficationDto noficationDto = new NoficationDto();

		List<Map<String, Object>> unreadCounts = new ArrayList<>();

		// References가 있을 경우, 첫 번째 reference에게만 알림 전송
		if (references != null && !references.isEmpty()) {
			Long firstReference = references.get(0);
			noficationDto.setNofication_content(nofication_content);
			noficationDto.setNofication_receive_no(firstReference);
			noficationDto.setNofication_title(nofication_title);
			noficationDto.setNofication_type(nofication_type);
			noficationDto.setMember_no(senderNo);
			long notificationPk = noficationService.insertAlarmPk(); // pk값 추가

			if (noficationService.insertAlarm(noficationDto) > 0) {
				Map<String, Object> memberUnreadCount = new HashMap<>();
				memberUnreadCount.put("memberNo", firstReference);
				memberUnreadCount.put("nofication_pk", notificationPk);//같이 넘길 값
				unreadCounts.add(memberUnreadCount);
			}
		} else if (approvers != null && !approvers.isEmpty()) {
			// Reference가 없을 경우, 첫 번째 approver에게 알림 전송
			Long firstApprover = approvers.get(0);
			noficationDto.setNofication_content(nofication_content);
			noficationDto.setNofication_receive_no(firstApprover);
			noficationDto.setNofication_title(nofication_title);
			noficationDto.setNofication_type(nofication_type);
			noficationDto.setMember_no(senderNo);
			long notificationPk = noficationService.insertAlarmPk(); // pk값 추가

			if (noficationService.insertAlarm(noficationDto) > 0) {
				Map<String, Object> memberUnreadCount = new HashMap<>();
				memberUnreadCount.put("memberNo", firstApprover);
				memberUnreadCount.put("nofication_pk", notificationPk);//같이 넘길 값
				unreadCounts.add(memberUnreadCount);
			}
		}

		for (WebSocketSession s : sessions.values()) {
			if (s.isOpen()) {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("type", "approvalAlarm");
				responseMap.put("title", nofication_title);
				responseMap.put("content", noficationDto.getNofication_content());
				responseMap.put("data", unreadCounts);
				String currentTime = getCurrentFormattedDateTime();
				responseMap.put("timestamp", currentTime);
				String unreadMessage = objectMapper.writeValueAsString(responseMap);
				s.sendMessage(new TextMessage(unreadMessage));
			}
		}
	}
	
	//[전주영] 전자결재 등록 알림 (참조자)
	private void handleApprovalReviewersAlarm(Map<String, Object> jsonMap, WebSocketSession session, String type)
			throws Exception {
		
		Object sendNoobj = jsonMap.get("memberNo");

		Long senderNo = null;

		if (sendNoobj instanceof String) {
			senderNo = Long.parseLong((String) sendNoobj);
		} else if (sendNoobj instanceof Integer) {
			senderNo = ((Integer) sendNoobj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}		
		
	    List<Long> reviewers = null;

	    if (jsonMap.get("reviewers") instanceof List) {
	        reviewers = ((List<?>) jsonMap.get("reviewers")).stream().map(value -> {
	            if (value instanceof String) {
	                return Long.parseLong((String) value);  
	            } else if (value instanceof Integer) {
	                return ((Integer) value).longValue(); 
	            } else if (value instanceof Long) {
	                return (Long) value;
	            } else {
	                throw new IllegalArgumentException("타입 오류");
	            }
	        }).collect(Collectors.toList());
		}
	    	
		MemberDto member = memberService.selectMemberOne(senderNo);

		String nofication_content = member.getMember_name()+"님이 기안한 결재 문서의 참조자로 지정 되었습니다.";
		
		String nofication_title = "전자결재";
		int nofication_type = 8;

		NoficationDto noficationDto = new NoficationDto();

		List<Map<String, Object>> unreadCounts = new ArrayList<>();	    
	    
		if (reviewers != null) {
			for (Long reviewer : reviewers) {
				noficationDto.setNofication_content(nofication_content);
				noficationDto.setNofication_receive_no(reviewer);
				noficationDto.setNofication_title(nofication_title);
				noficationDto.setNofication_type(nofication_type);
				noficationDto.setMember_no(senderNo);
				long notificationPk = noficationService.insertAlarmPk(); // pk값 추가

				if (noficationService.insertAlarm(noficationDto) > 0) {
					Map<String, Object> memberUnreadCount = new HashMap<>();
					memberUnreadCount.put("memberNo", reviewer);
					memberUnreadCount.put("nofication_pk", notificationPk);//같이 넘길 값
					unreadCounts.add(memberUnreadCount);
				}
			}
		}

		for (WebSocketSession s : sessions.values()) {
			if (s.isOpen()) {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("type", "approvalReviewsAlarm");
				responseMap.put("title", nofication_title);
				responseMap.put("content", noficationDto.getNofication_content());
				responseMap.put("data", unreadCounts);
				String currentTime = getCurrentFormattedDateTime();
				responseMap.put("timestamp", currentTime);
				String unreadMessage = objectMapper.writeValueAsString(responseMap);
				s.sendMessage(new TextMessage(unreadMessage));
			}
		}	    
	}
	
	// [전주영] 전자결재 승인 알림
	private void handleAppApproveAlarm(Map<String, Object> jsonMap, WebSocketSession session, String type)
			throws Exception {

		// 결재자
		Object sendNoObj = jsonMap.get("memberNo");
		// 기안자
		Object approvalMemberObj = jsonMap.get("appprovalMemberNo");
		// 문서 번호
		Object approvalNoObj = jsonMap.get("approvalNo");

		Long senderNo;
		Long approvalMemberNo;
		Long approvalNo;

		if (sendNoObj instanceof String) {
			senderNo = Long.parseLong((String) sendNoObj);
		} else if (sendNoObj instanceof Integer) {
			senderNo = ((Integer) sendNoObj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}

		if (approvalMemberObj instanceof String) {
			approvalMemberNo = Long.parseLong((String) approvalMemberObj);
		} else if (approvalMemberObj instanceof Integer) {
			approvalMemberNo = ((Integer) approvalMemberObj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}

		if (approvalNoObj instanceof String) {
			approvalNo = Long.parseLong((String) approvalNoObj);
		} else if (approvalNoObj instanceof Integer) {
			approvalNo = ((Integer) approvalNoObj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}
		
		MemberDto member = memberService.selectMemberOne(approvalMemberNo);
		
		ApprovalDto approval = approvalService.selectApprovalOne(approvalNo);
		
		List<ApprovalFlowDto> approvalDtos = approvalService.getApprovalFlows(approvalNo);
		Long nextApproverNo = findNextApproverMemberNo(approvalDtos, senderNo);

		String nofication_content = member.getMember_name()+"님이 기안한 결재 문서의 결재 차례가 되었습니다.";
		String nofication_title = "전자결재";
		int nofication_type = 9;

		NoficationDto noficationDto = new NoficationDto();

		List<Map<String, Object>> unreadCounts = new ArrayList<>();

		if (nextApproverNo != null) {
	         noficationDto.setNofication_content(nofication_content);
	         noficationDto.setNofication_receive_no(nextApproverNo);
	         noficationDto.setNofication_title(nofication_title);
	         noficationDto.setNofication_type(nofication_type);
	         noficationDto.setMember_no(senderNo);
			long notificationPk = noficationService.insertAlarmPk(); // pk값 추가

	         if (noficationService.insertAlarm(noficationDto) > 0) {
	            Map<String, Object> memberUnreadCount = new HashMap<>();
	            memberUnreadCount.put("memberNo", nextApproverNo);
				memberUnreadCount.put("nofication_pk", notificationPk);//같이 넘길 값
	            unreadCounts.add(memberUnreadCount);
	         }

		} else {
	         noficationDto.setNofication_content('"'+approval.getApproval_title()+'"'+"문서의 결재가 완료되었습니다.");
	         noficationDto.setNofication_receive_no(approvalMemberNo);
	         noficationDto.setNofication_title(nofication_title);
	         noficationDto.setNofication_type(nofication_type);
	         noficationDto.setMember_no(senderNo);
			 long notificationPk = noficationService.insertAlarmPk(); // pk값 추가

	         if (noficationService.insertAlarm(noficationDto) > 0) {
	            Map<String, Object> memberUnreadCount = new HashMap<>();
	            memberUnreadCount.put("memberNo", approvalMemberNo);
				memberUnreadCount.put("nofication_pk", notificationPk);//같이 넘길 값
	            unreadCounts.add(memberUnreadCount);
	         }
		}
		
	      for (WebSocketSession s : sessions.values()) {
	          if (s.isOpen()) {
	             ObjectMapper objectMapper = new ObjectMapper();
	             Map<String, Object> responseMap = new HashMap<>();
	             responseMap.put("type", "appApproveAlarm");
	             responseMap.put("title", nofication_title);
	             responseMap.put("content", noficationDto.getNofication_content());
	             responseMap.put("data", unreadCounts);
				  String currentTime = getCurrentFormattedDateTime();
				  responseMap.put("timestamp", currentTime);
	             String unreadMessage = objectMapper.writeValueAsString(responseMap);
	             s.sendMessage(new TextMessage(unreadMessage));
	          }
	       }
	}
	
	// 다음 결재자 
	public Long findNextApproverMemberNo(List<ApprovalFlowDto> approvalDtos, Long senderNo) {
		for (int i = 0; i < approvalDtos.size(); i++) {
			ApprovalFlowDto currentApproval = approvalDtos.get(i);

			if (currentApproval.getMember_no().equals(senderNo)) {
				for (int j = i + 1; j < approvalDtos.size(); j++) {
					ApprovalFlowDto nextApproval = approvalDtos.get(j);
					if (nextApproval.getApproval_flow_order() != null) {
						return nextApproval.getMember_no();
					}
				}
				return null;
			}
		}
		return null;
	}
				
	// [전주영] 전자결재 반려 알림
	private void handleAppRejectAlarm(Map<String, Object> jsonMap, WebSocketSession session, String type)
			throws Exception {

		// 결재자
		Object sendNoObj = jsonMap.get("memberNo");
		// 기안자
		Object approvalMemberObj = jsonMap.get("appprovalMemberNo");
		// 문서 번호
		Object approvalNoObj = jsonMap.get("approvalNo");

		Long senderNo;
		Long approvalMemberNo;
		Long approvalNo;

		if (sendNoObj instanceof String) {
			senderNo = Long.parseLong((String) sendNoObj);
		} else if (sendNoObj instanceof Integer) {
			senderNo = ((Integer) sendNoObj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}

		if (approvalMemberObj instanceof String) {
			approvalMemberNo = Long.parseLong((String) approvalMemberObj);
		} else if (approvalMemberObj instanceof Integer) {
			approvalMemberNo = ((Integer) approvalMemberObj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}

		if (approvalNoObj instanceof String) {
			approvalNo = Long.parseLong((String) approvalNoObj);
		} else if (approvalNoObj instanceof Integer) {
			approvalNo = ((Integer) approvalNoObj).longValue();
		} else {
			throw new IllegalArgumentException("타입 오류");
		}
		
		ApprovalDto approval = approvalService.selectApprovalOne(approvalNo);
		
		String nofication_content = '"'+approval.getApproval_title()+'"'+" 문서의 결재가 반려되었습니다.";
		String nofication_title = "전자결재";
		int nofication_type = 10;

		NoficationDto noficationDto = new NoficationDto();

		List<Map<String, Object>> unreadCounts = new ArrayList<>();

		noficationDto.setNofication_content(nofication_content);
		noficationDto.setNofication_receive_no(approvalMemberNo);
		noficationDto.setNofication_title(nofication_title);
		noficationDto.setNofication_type(nofication_type);
		noficationDto.setMember_no(senderNo);
		long notificationPk = noficationService.insertAlarmPk(); // pk값 추가

		if (noficationService.insertAlarm(noficationDto) > 0) {
			Map<String, Object> memberUnreadCount = new HashMap<>();
			memberUnreadCount.put("memberNo", approvalMemberNo);
			memberUnreadCount.put("nofication_pk", notificationPk);//같이 넘길 값
			unreadCounts.add(memberUnreadCount);
		}

		for (WebSocketSession s : sessions.values()) {
			if (s.isOpen()) {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("type", "appRejectAlarm");
				responseMap.put("title", nofication_title);
				responseMap.put("content", noficationDto.getNofication_content());
				responseMap.put("data", unreadCounts);
				String currentTime = getCurrentFormattedDateTime();
				responseMap.put("timestamp", currentTime);
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
