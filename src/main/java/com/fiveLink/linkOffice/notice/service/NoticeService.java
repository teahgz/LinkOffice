package com.fiveLink.linkOffice.notice.service;



import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.repository.MemberRepository;
import com.fiveLink.linkOffice.notice.controller.NoticeApiController;
import com.fiveLink.linkOffice.notice.domain.Notice;
import com.fiveLink.linkOffice.notice.domain.NoticeDto;
import com.fiveLink.linkOffice.notice.repository.NoticeRepository;

@Service
public class NoticeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(NoticeApiController.class);
    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository, MemberRepository memberRepository) {
        this.noticeRepository = noticeRepository;
        this.memberRepository = memberRepository;
    }

    public Notice createNotice(NoticeDto dto) {
        Long noticeWriter = dto.getMember_no(); 
        Member member = memberRepository.findBymemberNo(noticeWriter);
        
        Notice notice = Notice.builder()
                .noticeTitle(dto.getNotice_title())
                .noticeContent(dto.getNotice_content())
                .noticeCreateDate(dto.getNotice_create_date())
                .noticeImportance(dto.getNotice_importance())
                .noticeOriImg(dto.getNotice_ori_img())
                .noticeNewImg(dto.getNotice_new_img())
                .member(member)
                .build();
        return noticeRepository.save(notice);
    }

    // managerName을 통해 member_no 조회하는 메서드
    public Long findMemberNoByManagerName(String managerName) {
        Member member = memberRepository.findByMemberName(managerName);
        return member.getMemberNo();
    }

    // 멤버 넘버로 멤버 이름 조회
    public String findNoticeNameByNumber(String memberNumber) {
        return noticeRepository.findMemberNameByMemberNumber(memberNumber);
    }
    
    public Page<NoticeDto> getAllNoticePage(Pageable pageable, NoticeDto searchDto){
    	Page<Object[]> results = null;
    	
    	String searchText = searchDto.getSearch_text();
    	if (searchText != null && !searchText.isEmpty()) {
    	    int searchType = searchDto.getSearch_type();
    	    switch (searchType) {
    	    	// 전체 검색
    	        case 1:
    	        	results = noticeRepository.findNoticesByTitleOrContentContainingWithMember(searchText, pageable);
    	            break;
    	        // 제목
    	        case 2:
    	            results = noticeRepository.findNoticesByTitleWithMember(searchText, pageable);
    	            break;
    	        // 내용 검색
    	        case 3:
    	            results = noticeRepository.findNoticesByContentWithMember(searchText, pageable);
    	            break;
    	         // 작성자 검색
    	        case 4:
    	            results = noticeRepository.findNoticesByMember(searchText, pageable);
    	            break;
    	    	}
    	    }else {
        	    results = noticeRepository.findNoticesAllWithMember(pageable);
        	}
    	List<NoticeDto> noticeDtoList = convertToDtoList(results.getContent());
        return new PageImpl<>(noticeDtoList, pageable, results.getTotalElements());
    	}
    
    private List<NoticeDto> convertToDtoList(List<Object[]> results) {
        return results.stream().map(result -> {
            Notice notice = (Notice) result[0];
            String memberName = (String) result[1];
            return NoticeDto.builder()
                    .notice_no(notice.getNoticeNo())
                    .notice_title(notice.getNoticeTitle())
                    .notice_content(notice.getNoticeContent())
                    .notice_create_date(notice.getNoticeCreateDate())
                    .notice_update_date(notice.getNoticeUpdateDate())
                    .notice_importance(notice.getNoticeImportance())
                    .notice_new_img(notice.getNoticeNewImg())
                    .notice_ori_img(notice.getNoticeOriImg())
                    .member_name(memberName)
                    .build();
        }).collect(Collectors.toList());
    }
    
    public List<NoticeDto> getNoticeByNo(Long noticeNo) {
        List<Object[]> results = noticeRepository.findNoticesWithMemberName(noticeNo);
        return convertToDtoList(results);
    }
    
}
