package com.fiveLink.linkOffice.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
	
	// 전자결재 서명 등록
	@ResponseBody
	@PostMapping("/employee/member/digitalname/{member_no}")
	public Map<String, String> digitalnameUpdate( @PathVariable("member_no") Long memberNo,@RequestParam("signatureData") String signatureData) {
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
	                
                    if(memberFileService.delete(memberdto.getMember_no()) > 0) {
                    	response.put("res_msg", "기존 파일 삭제 완료되었습니다.");
                    }else {
                    	response.put("res_msg", "기존 파일 삭제 중 오류가 발생되었습니다.");
                    }

	            }
	            
	            if (memberService.updateMemberDigital(memberdto) != null) {
                    response.put("res_code", "200");
                    response.put("res_msg", "파일 업로드가 완료되었습니다.");
                 
                }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("res_msg", "서버 오류가 발생하였습니다.");
	    }
	    return response;
	}
	
	@ResponseBody
	@PostMapping("/employee/member/myedit/{member_no}")
	public Map<String,String> profileUpdate(  @PathVariable("member_no") Long memberNo,
            @RequestParam(name = "file", required = false) MultipartFile file, 
            @RequestParam(name = "roadAddress") String roadAddress,  
            @RequestParam(name = "detailAddress") String detailAddress, 
            @RequestParam(name = "newPassword") String newPassword){
		 Map<String, String> response = new HashMap<>();
		    response.put("res_code", "404");
		    response.put("res_msg", "파일 등록 중 오류가 발생하였습니다.");
		    
		    MemberDto memberdto = memberService.selectMemberOne(memberNo); 
		    
		    String newAdr = roadAddress + detailAddress;
		    if(!newAdr.isEmpty()) {
		    	memberdto.setMember_address(newAdr);
		    }
		    if(!newPassword.isEmpty()) {
		    	memberdto.setMember_pw(newPassword);
		    }
		    if(file != null && "".equals(file.getOriginalFilename()) == false) {
		    	String saveFileName = memberFileService.uploadProfile(file);
		    	if(saveFileName != null) {
		    		memberdto.setMember_ori_profile_img(file.getOriginalFilename());
		    		memberdto.setMember_new_profile_img(saveFileName);
		    	} else {
		    		response.put("res_msg", "파일 업로드 실패");
		    	}
		    	
		    }
		    
		    if(memberService.updateMemberProfile(memberdto) != null) {
		    	response.put("res_code", "200");
		    	response.put("res_msg", "정보 수정 성공하였습니다.");
		    }
		    return response;
	}

}
