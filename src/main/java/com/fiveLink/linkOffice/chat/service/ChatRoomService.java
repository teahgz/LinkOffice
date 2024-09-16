package com.fiveLink.linkOffice.chat.service;

import com.fiveLink.linkOffice.chat.domain.ChatRoom;
import com.fiveLink.linkOffice.chat.domain.ChatRoomDto;
import com.fiveLink.linkOffice.chat.repository.ChatRoomRepository;
import com.fiveLink.linkOffice.mapper.ChatMapper;
import com.fiveLink.linkOffice.vacation.domain.VacationStandard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMapper chatMapper;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository,ChatMapper chatMapper) {
        this.chatRoomRepository =chatRoomRepository;
        this.chatMapper =chatMapper;
    }

    public Long createRoomOne(ChatRoomDto dto) {
        ChatRoom room = dto.toEntity();
        ChatRoom savedRoom = chatRoomRepository.save(room);
        return savedRoom.getChatRoomNo();
    }


    public int countRoomNo() {
        return chatMapper.countRoomNo();
    }

    public String searchPosition(Long memberNo){
        return chatMapper.searchPosition(memberNo);
    }
    public Long createRoomMany(ChatRoomDto dto) {
        ChatRoom room = dto.toEntity();
        ChatRoom savedRoom = chatRoomRepository.save(room);
        return savedRoom.getChatRoomNo();
    }

}
