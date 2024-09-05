package com.fiveLink.linkOffice.member.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.organization.domain.DepartmentDto;
import com.fiveLink.linkOffice.organization.service.DepartmentService;

@Controller
public class MemberViewController {
	
	private final MemberService memberService;
	private final DepartmentService departmentService;
	
	@Autowired
	public MemberViewController(MemberService memberService,DepartmentService departmentService) {
		this.memberService = memberService;
		this.departmentService = departmentService;
	}
	
	// 내정보 페이지
	@GetMapping("/employee/member/mypage/{member_no}")
	public String myPage(@PathVariable("member_no") Long memberNo, Model model) {
		List<MemberDto> memberdto = memberService.getMembersByNo(memberNo); 
	    model.addAttribute("memberdto", memberdto);
	    return "employee/member/mypage";
	}
	// 정보 수정 페이지
	@GetMapping("/employee/member/myedit/{member_no}")
	public String myedit(@PathVariable("member_no") Long memberNo, Model model) {
		List<MemberDto> memberdto = memberService.getMembersByNo(memberNo); 
	    model.addAttribute("memberdto", memberdto);
		return "employee/member/myedit";
	}
	
	// 정보 수정 페이지
	@GetMapping("/employee/member/digitalname/{member_no}")
	public String digitalname(@PathVariable("member_no") Long memberNo, Model model) {
	    List<MemberDto> memberDtoList = memberService.getMembersByNo(memberNo);
	    model.addAttribute("memberdto", memberDtoList);
	    return "employee/member/digitalname";
	}

	// 관리자 사원 등록 페이지
	@GetMapping("/admin/member/create")
	public String create(Model model) {
			// 로그인한 사원의 정보
	      Long memberNo = memberService.getLoggedInMemberNo();
	      // 번호
	      List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);
	      // 부서명 조회 
	      List<DepartmentDto> departments = departmentService.getAllDepartments();
	        
	        
	      model.addAttribute("memberdto", memberdto);
	      model.addAttribute("departments", departments);
		return "admin/member/create";
	}
}

