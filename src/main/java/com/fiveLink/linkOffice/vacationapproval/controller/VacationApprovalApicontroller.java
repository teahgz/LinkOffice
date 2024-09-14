package com.fiveLink.linkOffice.vacationapproval.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalDto;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFileDto;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFlowDto;
import com.fiveLink.linkOffice.vacationapproval.service.VacationApprovalFileService;
import com.fiveLink.linkOffice.vacationapproval.service.VacationApprovalService;

@Controller
public class VacationApprovalApicontroller {

	
	private final VacationApprovalService vacationApprovalService;
	private final VacationApprovalFileService vacationApprovalFileService;
	
	@Autowired
	public VacationApprovalApicontroller (VacationApprovalService vacationApprovalService, VacationApprovalFileService vacationApprovalFileService) {
		this.vacationApprovalService = vacationApprovalService;
		this.vacationApprovalFileService = vacationApprovalFileService;
	}
	
	@ResponseBody
	@PostMapping("/employee/vacationapproval/create")
	public Map<String, String> createVacationApproval(
	        @RequestParam(value = "vacationFile", required = false) MultipartFile file,
	        @RequestParam("vacationapprovalTitle") String vacationapprovalTitle,
	        @RequestParam("vacationtype") Long vacationtype,
	        @RequestParam("memberNo") Long memberNo,
	        @RequestParam("startDate") String startDate,
	        @RequestParam("endDate") String endDate,
	        @RequestParam("dateCount") String dateCount,
	        @RequestParam("vacationapprovalContent") String vacationapprovalContent,
	        @RequestParam("approvers") List<String> approvers,
	        @RequestParam("references") List<String> references,
	        @RequestParam("reviewers") List<String> reviewers) {
		

	    Map<String, String> response = new HashMap<>();
	    response.put("res_code", "404");
	    response.put("res_msg", "휴가 신청 중 오류가 발생했습니다.");

	    VacationApprovalDto vappdto = new VacationApprovalDto();
	    vappdto.setVacation_approval_title(vacationapprovalTitle);
	    vappdto.setVacation_type_no(vacationtype);
	    vappdto.setMember_no(memberNo);
	    vappdto.setVacation_approval_start_date(startDate);
	    vappdto.setVacation_approval_end_date(endDate);
	    vappdto.setVacation_approval_total_days(dateCount);
	    vappdto.setVacation_approval_content(vacationapprovalContent);
	    vappdto.setVacation_approval_status(0L); 

	    System.out.println(approvers);
	    System.out.println(references);
	    System.out.println(reviewers);
	    
	    
	    
	    boolean isFileUploaded = false;
	    
	    if (file != null && !file.isEmpty()) {
	        VacationApprovalFileDto vaFiledto = new VacationApprovalFileDto();
	        
	        String saveVacationFileName = vacationApprovalFileService.uploadVacation(file);
	        
	        if (saveVacationFileName != null) {
	            vaFiledto.setVacation_approval_file_ori_name(file.getOriginalFilename());
	            vaFiledto.setVacation_approval_file_new_name(saveVacationFileName);
	            vaFiledto.setVacation_approval_file_size(file.getSize());

	            if (vacationApprovalService.createVacationApproval(vappdto, vaFiledto) != null) {
	                response.put("res_code", "200");
	                response.put("res_msg", "휴가 신청이 완료되었습니다.");
	                isFileUploaded = true;
	            }
	        } 
	    }

	    if (!isFileUploaded) {
	        if (vacationApprovalService.createVacationApproval(vappdto) != null) {
	            response.put("res_code", "200");
	            response.put("res_msg", "휴가 신청이 완료되었습니다.");
	        }
	    }

	    return response;
	}
}
