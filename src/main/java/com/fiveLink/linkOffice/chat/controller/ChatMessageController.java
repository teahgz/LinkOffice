package com.fiveLink.linkOffice.chat.controller;

import com.fiveLink.linkOffice.chat.domain.ChatMemberDto;
import com.fiveLink.linkOffice.chat.domain.ChatMessage;
import com.fiveLink.linkOffice.chat.service.ChatMemberService;
import com.fiveLink.linkOffice.chat.service.ChatMessageService;
import com.fiveLink.linkOffice.chat.domain.ChatMessageDto;
import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.repository.MemberRepository;
import com.fiveLink.linkOffice.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller

public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    private final MemberService memberService;
    private final ChatMemberService chatMemberService;
    private final MemberRepository memberRepository;

    @Autowired
    public ChatMessageController(ChatMessageService chatMessageService, MemberService memberService, MemberRepository memberRepository,ChatMemberService chatMemberService){
        this.chatMessageService =chatMessageService;
        this.memberService =memberService;
        this.chatMemberService =chatMemberService;
        this.memberRepository = memberRepository;

    }

    @GetMapping("/api/chat/{member_no}")
    public String chatMessagePage(@PathVariable("member_no") Long memberNo, Model model){
        try {
            List<MemberDto> memberDtoList = memberService.getMembersByNo(memberNo);
            List<ChatMemberDto> chatList = chatMemberService.selectChatList(memberNo);
            if (memberDtoList.isEmpty()) {
                model.addAttribute("error", "No member found with the provided ID.");
                return "error";
            }

            model.addAttribute("memberdto", memberDtoList);
            model.addAttribute("chatList",chatList);
            // 디버깅을 위한 로그 출력 (프로덕션에서는 삭제 권장)
            System.out.println("Chat List: " + chatList);

            return "admin/chat/chatMessage";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while fetching member information.");
            return "error";
        }

    }
    @GetMapping("/api/chat/messages/{chat_room_no}")
    @ResponseBody
    public List<Map<String, Object>> chatMessages(@PathVariable("chat_room_no") Long chatRoomNo){
        try{
            return chatMessageService.getChatMessages(chatRoomNo);
        }catch (Exception e){
            e.printStackTrace();
            return Collections.emptyList();
        }
    }



}