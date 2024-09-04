package com.fiveLink.linkOffice.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberFileService;
import com.fiveLink.linkOffice.member.service.MemberService;

@Controller
public class MemberApiController {

private final MemberService memberService;
private final MemberFileService memberFileService;
	
	@Autowired
	public MemberApiController(MemberService memberService,MemberFileService memberFileService) {
		this.memberService = memberService;
		this.memberFileService = memberFileService;
	}
	
	@ResponseBody
	@PostMapping("/employee/member/digitalname/{member_no}")
	public Map<String, String> digitalnameUpdate(
	        @PathVariable("member_no") Long memberNo,
	        @RequestParam("signatureData") String signatureData) {
	    Map<String, String> response = new HashMap<>();
	    response.put("res_code", "404");
	    response.put("res_msg", "파일 등록 중 오류가 발생하였습니다.");
	    
	    MemberDto memberdto = memberService.selectMemberOne(memberNo); 
	     
	    try {
	        if (signatureData != null && !signatureData.isEmpty()) {
	            String newDigitalName = memberFileService.uploadDigital(signatureData);
	            if (newDigitalName != null) {
	                memberdto.setMember_ori_digital_img(signatureData);
	                memberdto.setMember_new_digital_img(newDigitalName);
	                
	                if (memberService.updateMemberDigital(memberdto) != null) {
	                    response.put("res_code", "200");
	                    response.put("res_msg", "파일 업로드가 완료되었습니다.");
	                }
	            } else {
	                response.put("res_msg", "파일 등록 중 오류가 발생하였습니다.");
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("res_msg", "서버 오류가 발생하였습니다.");
	    }
	    return response;
	}

}
