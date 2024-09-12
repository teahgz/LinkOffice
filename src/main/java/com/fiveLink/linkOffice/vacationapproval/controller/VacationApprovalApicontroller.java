package com.fiveLink.linkOffice.vacationapproval.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalDto;
import com.fiveLink.linkOffice.vacationapproval.service.VacationApprovalService;

@RestController
@RequestMapping("/employee/vacationapproval")
public class VacationApprovalApicontroller {

	
	private final VacationApprovalService vacationApprovalService;
	
	@Autowired
	public VacationApprovalApicontroller (VacationApprovalService vacationApprovalService) {
		this.vacationApprovalService = vacationApprovalService;
	}
	
	@ResponseBody
	@PostMapping("/create")
	public Map<String,String> createVacationApproval(@RequestBody VacationApprovalDto dto){
		Map<String,String> response = new HashMap<String,String>();
		response.put("res_code", "404");
		response.put("res_msg", "휴가 신청 중 오류가 발생했습니다.");
		
		dto.setVacation_approval_status(0L);
		
		if(vacationApprovalService.createVacationApproval(dto) != null) {
			response.put("res_code", "200");
			response.put("res_msg", "휴가 신청이 완료되었습니다.");
		}
		
		return response;
	}
}
