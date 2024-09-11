package com.fiveLink.linkOffice.notice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.notice.service.NoticeService;

@Controller
public class NoticeViewController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticeViewController.class);
    private final NoticeService noticeService;
    private final MemberService memberService;

    @Autowired
    public NoticeViewController(MemberService memberService, NoticeService noticeService) {
        this.memberService = memberService;
        this.noticeService = noticeService;
    }

    // 공지사항 생성 페이지
    @GetMapping("/admin/notice/create/{member_no}")
    public String noticeCreate(@PathVariable("member_no") Long memberNo, Model model) {
        // 멤버 정보 조회
        List<MemberDto> memberDto = memberService.getMembersByNo(memberNo);
        model.addAttribute("memberDto", memberDto);

        // 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String noticeNumber = authentication.getName();
        String noticeName = noticeService.findNoticeNameByNumber(noticeNumber);
        model.addAttribute("manager", noticeName);  

        return "admin/notice/notice_create";
    }
}