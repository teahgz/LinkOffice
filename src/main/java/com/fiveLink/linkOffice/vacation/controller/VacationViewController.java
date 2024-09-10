package com.fiveLink.linkOffice.vacation.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.vacation.domain.VacationDto;
import com.fiveLink.linkOffice.vacation.domain.VacationStandardDto;
import com.fiveLink.linkOffice.vacation.domain.VacationTypeDto;
import com.fiveLink.linkOffice.vacation.service.VacationService;

@Controller
public class VacationViewController {
    private static final Logger logger = LoggerFactory.getLogger(VacationViewController.class);


    private final MemberService memberService;
    private final VacationService vacationService;

    @Autowired
    public VacationViewController(MemberService memberService, VacationService vacationService) {
        this.memberService = memberService;
        this.vacationService = vacationService;
    }

    //휴가 생성 페이지 이동
    @GetMapping("/vacation/addVacation/{member_no}")
    public String addVacation(@PathVariable("member_no") Long memberNo, Model model) {
        // 로그에 정보 출력
        int countVacation = vacationService.countVacation();
        List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);

        List<VacationDto> vacationList = vacationService.selectVacationList();

        int countVacationType = vacationService.countVacationType();
        List<VacationTypeDto> vacationTypeList = vacationService.selectVacationTypeList();

        int countCheckOneYear = vacationService.countCheckOneYear();

        model.addAttribute("memberdto", memberdto);
        model.addAttribute("vacationList", vacationList);
        model.addAttribute("countVacation", countVacation);
        model.addAttribute("countVacationType", countVacationType);
        model.addAttribute("vacationTypeList", vacationTypeList);
        model.addAttribute("countCheckOneYear", countCheckOneYear);

        // 휴가 생성 페이지로 이동
        return "admin/vacation/addVacation";
    }

    //휴가 기준 페이지 이동
    @GetMapping("/vacation/vacationStandard/{member_no}")
    public String addStandard(@PathVariable("member_no") Long memberNo, Model model) {

        List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);
        int countStandard = vacationService.countStandard();

        List<VacationStandardDto> vacationStandard = vacationService.selectVacationStandard();
        model.addAttribute("memberdto", memberdto);
        model.addAttribute("countStandard", countStandard);
        model.addAttribute("vacationStandard", vacationStandard);
        //휴가 생성 페이지로 이동
        return "admin/vacation/vacation";
    }
}

