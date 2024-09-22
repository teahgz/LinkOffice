package com.fiveLink.linkOffice.chat.controller;

import com.fiveLink.linkOffice.chat.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

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
            @PathVariable Long currentChatRoomNo,
            @RequestBody Map<String, Object> requestBody) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long currentMember = Long.parseLong(requestBody.get("currentMember").toString());
            System.out.println("currentMember: " + currentMember);

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

}
