package com.fiveLink.linkOffice.approval.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
	
	// 관리자 전자결재 양식 등록 페이지
	@GetMapping("/admin/approval/create")
	public String adminApprovalCreate(Model model) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
		
		model.addAttribute("memberdto", memberdto);
		
		return "admin/approval/approval_create";
	}
	
	private Sort getSortOption(String sort) {
		if ("latest".equals(sort)) {
			return Sort.by(Sort.Order.desc("approvalFormCreateDate")); 
		} else if ("oldest".equals(sort)) {
			return Sort.by(Sort.Order.asc("approvalFormCreateDate")); 
		}
		return Sort.by(Sort.Order.desc("approvalFormCreateDate")); 
	}
	
	// 관리자 전자결재 양식함 페이지
	@GetMapping("/admin/approval/form")
	public String adminApprovalForm(Model model, ApprovalFormDto searchdto, @PageableDefault(size = 10, sort = "positionLevel", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(value = "sort", defaultValue = "latest") String sort) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
		
		Sort sortOption = getSortOption(sort);
		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);
		
		Page<ApprovalFormDto> formList = approvalFormService.getAllApprovalForms(sortedPageable, searchdto);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		formList.forEach(form -> {
			if(form.getApproval_form_create_date() != null) {
				String fomattedCreateDate = form.getApproval_form_create_date().format(formatter);
				form.setFormat_create_date(fomattedCreateDate);
			}
		});
		
		model.addAttribute("memberdto", memberdto);
		model.addAttribute("formList", formList.getContent());
		model.addAttribute("page", formList);
		model.addAttribute("searchDto", searchdto);
		model.addAttribute("currentSort", sort);
		
		return "admin/approval/approval_form";
	}
	
	// 관리자 전자결재 양식 상세 페이지
	@GetMapping("/admin/approval/detail/{form_no}")
	public String adminApprovalDetail(Model model, @PathVariable("form_no") Long formNo) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
		
		ApprovalFormDto formList = approvalFormService.getApprovalFormOne(formNo);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		if (formList.getApproval_form_create_date() != null) {
		    String formattedCreateDate = formList.getApproval_form_create_date().format(formatter);
		    formList.setFormat_create_date(formattedCreateDate);
		}
		
		model.addAttribute("memberdto", memberdto);
		model.addAttribute("formList", formList);
		return "admin/approval/approval_detail";
	}
	
	// 관리자 전자결재 양식 수정 페이지
	@GetMapping("/admin/approval/edit/{form_no}")
	public String adminApprovalEdit(Model model, @PathVariable("form_no") Long formNo) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
		
		ApprovalFormDto formList = approvalFormService.getApprovalFormOne(formNo);
		
		model.addAttribute("memberdto", memberdto);
		model.addAttribute("formList", formList);
		
		return "admin/approval/approval_edit";
	}
	
}
