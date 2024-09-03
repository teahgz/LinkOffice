package com.fiveLink.linkOffice.vacation.controller;

import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class VacationViewController {
    private static final Logger logger = LoggerFactory.getLogger(VacationViewController.class);


    private final MemberService memberService;

    @Autowired
    public VacationViewController(MemberService memberService) {
        this.memberService = memberService;
    }

    //휴가 생성 페이지 이동
    @GetMapping("/vacation/addVacation/{member_no}")
    public String addVacation(@PathVariable("member_no") Long memberNo, Model model) {
        // 로그에 정보 출력
        logger.info("Navigating to addVacation page for member_no: {}", memberNo);

        List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);
        model.addAttribute("memberdto", memberdto);

        // 휴가 생성 페이지로 이동
        return "admin/vacation/addVacation";
    }

}

