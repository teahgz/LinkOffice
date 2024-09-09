package com.fiveLink.linkOffice.approval.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fiveLink.linkOffice.approval.domain.ApprovalFormDto;
import com.fiveLink.linkOffice.approval.service.ApprovalFormService;
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;

@Controller
public class ApprovalViewController {

	private final MemberService memberService;
	private final ApprovalFormService approvalFormService;
	
	
	@Autowired
	public ApprovalViewController(MemberService memberService,ApprovalFormService approvalFormService) {
		this.memberService = memberService;
		this.approvalFormService = approvalFormService;
	}
	// 전자결재 양식 등록 페이지
	@GetMapping("/admin/approval/create/{member_no}")
	public String adminApprovalCreate(@PathVariable("member_no") Long memberNo, Model model) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
		
		model.addAttribute("memberdto", memberdto);
		
		return "admin/approval/approval_create";
	}
	
	// 전자결재 양식함 페이지
	@GetMapping("/admin/approval/form/{member_no}")
	public String adminApprovalForm(@PathVariable("member_no") Long memberNo, Model model) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
		
		// 등록된 양식 조회 후 model로 전달
		List<ApprovalFormDto> formList = approvalFormService.getAllApprovalForms();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		formList.forEach(form -> {
			if(form.getApproval_form_create_date() != null) {
				String fomattedCreateDate = form.getApproval_form_create_date().format(formatter);
				form.setFormat_create_date(fomattedCreateDate);
			}
		});
		
		model.addAttribute("memberdto", memberdto);
		model.addAttribute("formList", formList);
		
		return "admin/approval/approval_form";
	}
}
