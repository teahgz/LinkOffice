package com.fiveLink.linkOffice.chat.service;

import com.fiveLink.linkOffice.chat.domain.ChatMessage;
import com.fiveLink.linkOffice.chat.domain.ChatMessageDto;
import com.fiveLink.linkOffice.chat.repository.ChatMessageRepository;
import com.fiveLink.linkOffice.mapper.ChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMapper chatMapper;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatMapper chatMapper){
        this.chatMessageRepository =chatMessageRepository;
        this.chatMapper = chatMapper;
    }
    // 채팅 메시지를 저장하는 메서드
    public void saveChatMessage(ChatMessageDto chatMessageDto) {
        ChatMessage chat = chatMessageDto.toEntity();
        chatMessageRepository.save(chat);
    }
    public List<Map<String, Object>> getChatMessages(Long roomNo) {
        return chatMapper.getChatMessages(roomNo);
    }

}