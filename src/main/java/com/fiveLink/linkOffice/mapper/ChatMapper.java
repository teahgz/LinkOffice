package com.fiveLink.linkOffice.mapper;

import com.fiveLink.linkOffice.chat.domain.ChatMemberDto;
import com.fiveLink.linkOffice.chat.domain.ChatMessageDto;
import com.fiveLink.linkOffice.vacation.domain.VacationDto;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ChatMapper {
    //채팅방 목록 가져오기
    List<ChatMemberDto> selectChatList(Long memberNo);
    //채팅 내용 가져오기
    List<Map<String, Object>> getChatMessages(Long roomNo);
    //채팅방 갯수 번호
    int countRoomNo();
    //채팅방 목록 디비 입력 이름+부서
    String searchPosition(Long memberNo);
    //채팅방 이름 가져오기
    String selectChatRoomName(Map<String, Object> params);
    //채팅방 이름 수정
    int updateChatRoom(Map<String, Object> params);

    //채팅방 타입 가져오기
    int chatRoomType(Long chatRooNo);
    //같은 채팅방에 있는 사용자 번호
    List<Long> chatRoomMemberNo(Long chatRoomNo);
    //수정 채팅방 이름 가져오기
    String selectMemberChatRoomName(Long chatRoomNo);
    //채팅방 멤버 가져오기
    List<Long> getMemberInfo(Long chatRoomNo);

    //개인 채팅방 정보 가져오기
    List<ChatMemberDto> getMembersByChatRoomNo(Long chatRoomNo);

    // 채팅방 나가기
    int chatRoomOut(Map<String, Object> params);

    // 채팅방 고정
    int chatRoomPin(Map<String, Object> params);

    // 채팅방 고정 여부
    int selectChatPin(Map<String, Object> params);
    //참여자 수
    int countParicipant(Long chatRoomNo);

    // 채팅방 별 안읽은 메시지 읽기
    List<Long> markMessagesAsReadForChatRoom(Map<String, Object> params);
    //채팅방 별 안읽은 메시지 개수
    List<Map<String, Object>> getUnreadCounts(Long memberNo);
}
