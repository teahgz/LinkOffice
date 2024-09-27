package com.fiveLink.linkOffice.webSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiveLink.linkOffice.approval.domain.ApprovalFlowDto;
import com.fiveLink.linkOffice.approval.service.ApprovalService;
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
    private final ApprovalService approvalService;

    @Autowired
    public NoficationWebSocketHandler(ChatRoomService chatRoomService, NoficationService noficationService,
    		MemberRepository memberRepository,
    		ApprovalService approvalService) {
        this.chatRoomService = chatRoomService;
        this.noficationService = noficationService;
        this.memberRepository = memberRepository;
        this.approvalService = approvalService;
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
        } else if("notificationApproval".equals(type)) {
        	handleApprovalAlarm(jsonMap, session, type);
        } else if("notificationAppApprove".equals(type)) {
        	handleAppApproveAlarm(jsonMap, session, type);
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
    
	// [전주영] 휴가결재 등록 알림
	private void handleVacationApprovalAlarm(Map<String, Object> jsonMap, WebSocketSession session, String type) throws Exception {
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

	     String nofication_content = "결재 문서가 도착했습니다.";
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
	            noficationDto.setNofication_content("참조 문서가 도착했습니다.");
	            noficationDto.setNofication_receive_no(reviewer);
	            noficationDto.setNofication_title(nofication_title);
	            noficationDto.setNofication_type(nofication_type);
	            noficationDto.setMember_no(senderNo);
	            
	            if (noficationService.insertAlarm(noficationDto) > 0) {
	                Map<String, Object> memberUnreadCount = new HashMap<>();
	                memberUnreadCount.put("memberNo", reviewer);
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
	    		responseMap.put("content", nofication_content);
	    		responseMap.put("data", unreadCounts);
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

		List<Long> reviewers = null;
		if (notificationData.get("reviewers") instanceof List) {
			reviewers = ((List<?>) notificationData.get("reviewers")).stream().map(value -> {
				if (value instanceof String) {
					return Long.parseLong((String) value);
				} else if (value instanceof Integer) {
					return ((Integer) value).longValue();
				} else if (value instanceof Long) {
					return (Long) value;
				} else {
					throw new IllegalArgumentException("알 수 없는 타입: " + value.getClass().getName());
				}
			}).collect(Collectors.toList());
		}

		String nofication_content = "결재 문서가 도착했습니다.";
		String nofication_title = "전자결재";
		int nofication_type = 6;

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
				noficationDto.setNofication_content("참조 문서가 도착했습니다.");
				noficationDto.setNofication_receive_no(reviewer);
				noficationDto.setNofication_title(nofication_title);
				noficationDto.setNofication_type(nofication_type);
				noficationDto.setMember_no(senderNo);

				if (noficationService.insertAlarm(noficationDto) > 0) {
					Map<String, Object> memberUnreadCount = new HashMap<>();
					memberUnreadCount.put("memberNo", reviewer);
					unreadCounts.add(memberUnreadCount);
				}
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

		List<ApprovalFlowDto> approvaldto = approvalService.getApprovalFlows(approvalNo);

		System.out.println("결재자 : " + senderNo);
		System.out.println("기안자 : " + approvalMemberNo);
		System.out.println("문서번호 : " + approvalNo);
		System.out.println("문서흐름 : " + approvaldto);

		List<ApprovalFlowDto> approvalDtos = approvalService.getApprovalFlows(approvalNo);
		Long nextApproverNo = findNextApproverMemberNo(approvalDtos, senderNo);

		String nofication_content = "결재 문서가 도착했습니다.";
		String nofication_title = "전자결재";
		int nofication_type = 7;

		NoficationDto noficationDto = new NoficationDto();

		List<Map<String, Object>> unreadCounts = new ArrayList<>();

		if (nextApproverNo != null) {
	         noficationDto.setNofication_content(nofication_content);
	         noficationDto.setNofication_receive_no(nextApproverNo);
	         noficationDto.setNofication_title(nofication_title);
	         noficationDto.setNofication_type(nofication_type);
	         noficationDto.setMember_no(senderNo);

	         if (noficationService.insertAlarm(noficationDto) > 0) {
	            Map<String, Object> memberUnreadCount = new HashMap<>();
	            memberUnreadCount.put("memberNo", nextApproverNo);
	            unreadCounts.add(memberUnreadCount);
	         }

		} else {
	         noficationDto.setNofication_content("문서가 결재 완료되었습니다.");
	         noficationDto.setNofication_receive_no(approvalMemberNo);
	         noficationDto.setNofication_title(nofication_title);
	         noficationDto.setNofication_type(nofication_type);
	         noficationDto.setMember_no(senderNo);

	         if (noficationService.insertAlarm(noficationDto) > 0) {
	            Map<String, Object> memberUnreadCount = new HashMap<>();
	            memberUnreadCount.put("memberNo", approvalMemberNo);
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
	             String unreadMessage = objectMapper.writeValueAsString(responseMap);
	             s.sendMessage(new TextMessage(unreadMessage));
	          }
	       }


	}

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
				
				

	@Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }

}
