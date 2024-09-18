package com.fiveLink.linkOffice.vacationapproval.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fiveLink.linkOffice.member.repository.MemberRepository;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalDto;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFileDto;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFlowDto;
import com.fiveLink.linkOffice.vacationapproval.service.VacationApprovalFileService;
import com.fiveLink.linkOffice.vacationapproval.service.VacationApprovalService;

@Controller
public class VacationApprovalApicontroller {

	private final MemberRepository memberRepository;
	private final VacationApprovalService vacationApprovalService;
	private final VacationApprovalFileService vacationApprovalFileService;
	
	@Autowired
	public VacationApprovalApicontroller (MemberRepository memberRepository, VacationApprovalService vacationApprovalService, VacationApprovalFileService vacationApprovalFileService) {
		this.memberRepository = memberRepository;
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
	        @RequestParam("approvers") List<Long> approvers,
	        @RequestParam("references") List<Long> references,
	        @RequestParam("reviewers") List<Long> reviewers) {
		

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

	    
	    List<VacationApprovalFlowDto> approvalFlowDtos = new ArrayList<>();
	    int order = 1;

	    // 1. 합의자 (flow_role = 1)
	    for (Long referenceId : references) {
	        VacationApprovalFlowDto flowDto = new VacationApprovalFlowDto();
	        flowDto.setMember_no(referenceId);
	        flowDto.setVacation_approval_flow_role(1L); 
	        flowDto.setVacation_approval_flow_order((long) order++);
	        flowDto.setVacation_approval_flow_status(order == 2 ? 1L : 0L); 
	        approvalFlowDtos.add(flowDto);
	    }

	    // 2. 결재자 (flow_role = 2)
	    for (Long approverId : approvers) {
	        VacationApprovalFlowDto flowDto = new VacationApprovalFlowDto();
	        flowDto.setMember_no(approverId);
	        flowDto.setVacation_approval_flow_role(2L); 
	        flowDto.setVacation_approval_flow_order((long) order++);
	        flowDto.setVacation_approval_flow_status(order == 2 && references.isEmpty() ? 1L : 0L); 
	        approvalFlowDtos.add(flowDto);
	    }

	    // 3. 참조자 (flow_role = 0)
	    for (Long reviewerId : reviewers) {
	        VacationApprovalFlowDto flowDto = new VacationApprovalFlowDto();
	        flowDto.setMember_no(reviewerId);
	        flowDto.setVacation_approval_flow_role(0L); 
	        flowDto.setVacation_approval_flow_order(null); 
	        flowDto.setVacation_approval_flow_status(4L); 
	        approvalFlowDtos.add(flowDto);
	    }

	
	    
	    boolean isFileUploaded = false;
	    
	    if (file != null && !file.isEmpty()) {
	        VacationApprovalFileDto vaFiledto = new VacationApprovalFileDto();
	        
	        String saveVacationFileName = vacationApprovalFileService.uploadVacation(file);
	        
	        if (saveVacationFileName != null) {
	            vaFiledto.setVacation_approval_file_ori_name(file.getOriginalFilename());
	            vaFiledto.setVacation_approval_file_new_name(saveVacationFileName);
	            vaFiledto.setVacation_approval_file_size(file.getSize());

	            if (vacationApprovalService.createVacationApprovalFile(vappdto, vaFiledto, approvalFlowDtos) != null) {
	                response.put("res_code", "200");
	                response.put("res_msg", "휴가 신청이 완료되었습니다.");
	                isFileUploaded = true;
	            }
	        } 
	    }

	    if (!isFileUploaded) {
	        if (vacationApprovalService.createVacationApproval(vappdto,approvalFlowDtos) != null) {
	            response.put("res_code", "200");
	            response.put("res_msg", "휴가 신청이 완료되었습니다.");
	        }
	    }

	    return response;
	}
	
	// 휴가 결재 기안 취소 (상태변화)
	@ResponseBody
	@PutMapping("/employee/vacationapproval/delete/{vacationapproval_no}")
	public Map<String,String> employeeVacationApprovalDelete(@PathVariable("vacationapproval_no") Long vapNo){
		Map<String, String> response = new HashMap<>();
	    response.put("res_code", "404");
	    response.put("res_msg", "기안 취소 중 오류가 발생하였습니다.");
	    
	    VacationApprovalDto dto = vacationApprovalService.selectVacationApprovalOne(vapNo);
	    dto.setVacation_approval_status(3L);
	    
	    if(vacationApprovalService.deleteVacationApproval(dto) != null) {
	    	response.put("res_code", "200");
		    response.put("res_msg", " 기안 취소를 성공하였습니다.");			 
	    }
	    return response; 
	}
	
	// 휴가 결재 수정 
	@ResponseBody
	@PutMapping("/employee/vacationapproval/edit/{vacationapproval_no}")
	public Map<String,String> employeeVacationApprovalEdit(@PathVariable("vacationapproval_no") Long vapNo, @RequestParam(value = "vacationFile", required = false) MultipartFile file,
	        @RequestParam("vacationapprovalTitle") String vacationapprovalTitle,
	        @RequestParam("vacationtype") Long vacationtype,
	        @RequestParam("startDate") String startDate,
	        @RequestParam("endDate") String endDate,
	        @RequestParam("dateCount") String dateCount,
	        @RequestParam("vacationapprovalContent") String vacationapprovalContent,
	        @RequestParam("approvers") List<Long> approvers,
	        @RequestParam("references") List<Long> references,
	        @RequestParam("reviewers") List<Long> reviewers){
		Map<String, String> response = new HashMap<>();
	    response.put("res_code", "404");
	    response.put("res_msg", "수정 중 오류가 발생하였습니다.");
	    
	    System.out.println(file.getOriginalFilename());
	    System.out.println(vacationapprovalTitle);
	    System.out.println(vacationtype);
	    System.out.println(startDate);
	    System.out.println(endDate);
	    System.out.println(dateCount);
	    System.out.println(vacationapprovalContent);
	    System.out.println(approvers);
	    System.out.println(references);
	    System.out.println(reviewers);
	    
	    return response;
	}
}
