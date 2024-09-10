package com.fiveLink.linkOffice.approval.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fiveLink.linkOffice.approval.domain.ApprovalFormDto;
import com.fiveLink.linkOffice.approval.service.ApprovalFormService;

@Controller
public class ApprovalApiController {
	
	private final ApprovalFormService approvalFormService;
	
	@Autowired
	public ApprovalApiController( ApprovalFormService approvalFormService) {
		this.approvalFormService = approvalFormService;
	}
		@ResponseBody
		@PostMapping("/admin/approval/create")
		public Map<String,String> handleFormSubmission(@RequestParam("approval_title") String approvalTitle, @RequestParam("editor_content") String editorContent) {
			Map<String, String> response = new HashMap<>();
		    response.put("res_code", "404");
		    response.put("res_msg", "파일 등록 중 오류가 발생하였습니다.");
			 ApprovalFormDto dto = new ApprovalFormDto();
			 dto.setApproval_form_title(approvalTitle);  
			 dto.setApproval_form_content(editorContent); 
			 System.out.println(dto);
			    
			 approvalFormService.saveApprovalForm(dto);
			
		    return response; 

		}
}
