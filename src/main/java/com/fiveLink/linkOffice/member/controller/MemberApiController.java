package com.fiveLink.linkOffice.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fiveLink.linkOffice.member.service.MemberService;

@Controller
public class MemberApiController {

private final MemberService memberService;
	
	@Autowired
	public MemberApiController(MemberService memberService) {
		this.memberService = memberService;
	}
	// 전자결재 이미지 수정중
	
}
