package com.fiveLink.linkOffice.chat.service;

import com.fiveLink.linkOffice.chat.domain.ChatMemberDto;
import com.fiveLink.linkOffice.chat.domain.ChatMessage;
import com.fiveLink.linkOffice.chat.domain.ChatMessageDto;
import com.fiveLink.linkOffice.chat.repository.ChatMemberRepository;
import com.fiveLink.linkOffice.chat.repository.ChatMessageRepository;
import com.fiveLink.linkOffice.mapper.ChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMemberService {
    private final ChatMemberRepository chatMemberRepository;
    private final ChatMapper chatMapper;


    @Autowired
    public ChatMemberService(ChatMemberRepository chatMemberRepository, ChatMapper chatMapper) {
        this.chatMemberRepository =chatMemberRepository;
        this.chatMapper =chatMapper;

    }

    public List<ChatMemberDto> selectChatList(Long memberNo){

        return chatMapper.selectChatList(memberNo);

    }


}

