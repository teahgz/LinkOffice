package com.fiveLink.linkOffice.chat.controller;

import com.fiveLink.linkOffice.chat.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService){
        this.chatRoomService =chatRoomService;
    }

    @PostMapping("/api/chat/out/{currentChatRoomNo}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> chatRoomOut(
            @PathVariable("currentChatRoomNo") Long currentChatRoomNo,
            @RequestBody Map<String, Object> requestBody) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long currentMember = Long.parseLong(requestBody.get("currentMember").toString());

            // 현재 사용자가 채팅방에서 나가는 로직 처리
            int result = chatRoomService.chatRoomOut(currentChatRoomNo, currentMember);

            if (result > 0) {
                response.put("success", true);
                response.put("message", "채팅방에서 성공적으로 나갔습니다.");

            } else {
                response.put("success", false);
                response.put("message", "채팅방 나가기에 실패했습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.ok(response);
    }
    //채팅방 고정
    @PostMapping("/api/chat/pin/{currentChatRoomNo}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> chatRoomPin(
            @PathVariable("currentChatRoomNo") Long currentChatRoomNo,
            @RequestBody Map<String, Object> requestBody) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long currentMember = Long.parseLong(requestBody.get("currentMember").toString());
            int status = (int) requestBody.get("statusValue");

            String updatedAtStr = requestBody.get("updatedAt").toString();
            LocalDateTime updateTime = null;
            if (status == 1) {
                if(updatedAtStr != null && !updatedAtStr.isEmpty()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                    updateTime = LocalDateTime.parse(updatedAtStr, formatter);
                }
            }
            else if (status == 0) {
                updateTime = null;
            }

            
            // 현재 사용자가 채팅방에서 나가는 로직 처리
            int result = chatRoomService.chatRoomPin(currentChatRoomNo, currentMember, status, updateTime);

            if (result > 0) {
                response.put("success", true);
                response.put("message", "채팅방에서 성공적으로 고정되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "채팅방 고정을 실패했습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/chat/pin/status/{chatRoomNo}/{currentMember}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkPinStatus(@PathVariable("chatRoomNo") Long chatRoomNo, @PathVariable("currentMember") Long currentMember) {
        System.out.println("test : "+ chatRoomNo);
        int status =  chatRoomService.selectChatPin(chatRoomNo, currentMember);
        Map<String, Object> response = new HashMap<>();
        response.put("isPinned", status);
        return ResponseEntity.ok(response);
    }
    //참여자 수
    @GetMapping("/api/chat/participants/count/{chatRoomNo}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> countParicipant(@PathVariable("chatRoomNo") Long chatRoomNo) {
        int status =  chatRoomService.countParicipant(chatRoomNo);
        Map<String, Object> response = new HashMap<>();
        response.put("count", status);
        return ResponseEntity.ok(response);
    }
    //중복 확인
    @PostMapping("/api/chat/checkDuplicateChatRoom")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkDuplicateChatRoom( @RequestBody Map<String, Object> requestBody) {
        Long memberNo = Long.parseLong(requestBody.get("currentMemberNo").toString());
        List<Long> selectedMembers = ((List<?>) requestBody.get("selectedMembers")).stream()
                .map(member -> Long.parseLong(member.toString()))
                .collect(Collectors.toList());
        System.out.println("test : "+ selectedMembers);
        System.out.println("test : "+ memberNo);
        boolean isDuplicate = chatRoomService.isDuplicateChatRoom(memberNo, selectedMembers);

        Map<String, Object> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);

        return ResponseEntity.ok(response);
    }
}
