package com.fiveLink.linkOffice.notice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.member.domain.Member;
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
}
