package com.fiveLink.linkOffice.member.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;

@Controller
public class MemberViewController {
	
	private final MemberService memberService;
	
	@Autowired
	public MemberViewController(MemberService memberService) {
		this.memberService = memberService;
	}
	
	@GetMapping("/employee/member/mypage/{member_no}")
	public String myPage(@PathVariable("member_no") Long memberNo, Model model) {
		List<MemberDto> memberdto = memberService.getMembersByNo(memberNo); 
	    model.addAttribute("memberdto", memberdto);
	    System.out.println(memberdto);
	    return "employee/member/mypage";
	}
	
	@GetMapping("/employee/member/myedit")
	public String myedit() {
		return "employee/member/myedit";
	}
}
