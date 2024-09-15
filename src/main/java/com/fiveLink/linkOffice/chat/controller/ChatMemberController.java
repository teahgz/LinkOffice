//package com.fiveLink.linkOffice.chat.controller;
//
//import com.fiveLink.linkOffice.chat.domain.ChatMemberDto;
//import com.fiveLink.linkOffice.chat.domain.ChatRoom;
//import com.fiveLink.linkOffice.chat.domain.ChatRoomDto;
//import com.fiveLink.linkOffice.chat.service.ChatMemberService;
//import com.fiveLink.linkOffice.chat.service.ChatRoomService;
//import com.fiveLink.linkOffice.member.domain.MemberDto;
//import com.fiveLink.linkOffice.vacation.domain.VacationTypeDto;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Controller
//public class ChatMemberController {
//    private final ChatMemberService chatMemberService;
//    private final ChatRoomService chatRoomService;
//
//    @Autowired
//    public ChatMemberController(ChatMemberService chatMemberService, ChatRoomService chatRoomService){
//        this.chatMemberService =chatMemberService;
//        this.chatRoomService =chatRoomService;
//    }
//    //채팅방 생성하기
//    @PostMapping("/api/chat/memberAddRoom")
//    @ResponseBody
//    public Map<String, String> chatRoomAdd(@RequestBody Map<String, Object> payload){
//        Map<String, String> resultMap = new HashMap<>();
//        resultMap.put("res_code", "404");
//        resultMap.put("res_msg", "휴가 생성 중 오류가 발생했습니다.");
//
//
//        try {
//            List<String> members = (List<String>) payload.get("members");
//            Long currentMemberNo = Long.parseLong((String) payload.get("currentMemberNo")); // 변환
//            List<String> names = (List<String>) payload.get("names");
//            String currentMemberName = (String) payload.get("currentMemberName");
//
//            ChatRoomDto dto = new ChatRoomDto();
//
//            if(members.size() == 1){
//                //먼저 채팅방 먼저 만들기
//                dto.setChat_room_type(0);//1:1 채팅방 타입
//                Long chatRoomNo = chatRoomService.createRoomOne(dto);
//
//
//                ChatMemberDto memberDto = new ChatMemberDto();
//                memberDto.setMember_no(Long.valueOf(members.get(0)));//초대받은 사람에 대한 정보
//                memberDto.setChat_room_no(chatRoomNo); //채팅 방 번호
//                memberDto.setChat_member_room_name(currentMemberName);//현재 로그인 된 사람 정보
//                if(chatMemberService.createMemberRoomOne(memberDto)>0){
//                    ChatMemberDto memberDto2 = new ChatMemberDto();
//                    memberDto2.setMember_no(currentMemberNo);//초대받은 사람에 대한 정보
//                    memberDto2.setChat_room_no(chatRoomNo); //채팅 방 번호
//                    memberDto2.setChat_member_room_name(names.get(0));//현재 로그인 된 사람 정보
//                    if(chatMemberService.createMemberRoomOne(memberDto2)>0){
//                        resultMap.put("res_code", "200");
//                        resultMap.put("res_msg", "생성 완료");
//
//                    }
//                }
//
//            }
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            resultMap.put("res_code", "404");
//            resultMap.put("res_msg", "처리 중 오류가 발생했습니다.");
//
//        }
//
//
//        return resultMap;
//    }
//}
