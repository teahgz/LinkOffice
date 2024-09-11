package com.fiveLink.linkOffice.vacationapproval.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.vacation.domain.VacationTypeDto;
import com.fiveLink.linkOffice.vacation.service.VacationService;

@Controller
public class VacationApprovalController {
	
	private final MemberService memberService;
    private final VacationService vacationService;
	
    @Autowired
    public VacationApprovalController(MemberService memberService, VacationService vacationService) {
        this.memberService = memberService;
        this.vacationService = vacationService;
    }
    
 // 사용자 휴가 결재 등록 페이지
 	@GetMapping("/employee/vacationapproval/create/{member_no}")
 	public String employeeVacationApprovalCreate(Model model, @PathVariable("member_no") Long memberNo) {
 		List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);
 		List<VacationTypeDto> vacationTypeList = vacationService.selectVacationTypeList();
 		
 		model.addAttribute("memberdto", memberdto);
 		model.addAttribute("vacationTypeList", vacationTypeList);
 		
 		return "employee/vacationapproval/vacationapproval_create";
 	}
}
