package com.fiveLink.linkOffice.approval.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;

@Controller
public class ApprovalViewController {

	private final MemberService memberService;
	
	
	@Autowired
	public ApprovalViewController(MemberService memberService) {
		this.memberService = memberService;
	}
	// 전자결재 양식 등록 페이지
	@GetMapping("/admin/approval/create/{member_no}")
	public String adminApprovalCreate(@PathVariable("member_no") Long memberNo, Model model) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
		
		model.addAttribute("memberdto", memberdto);
		
		return "admin/approval/approval_create";
	}
}
