package com.fiveLink.linkOffice.vacationapproval.controller;

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

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.vacation.domain.VacationTypeDto;
import com.fiveLink.linkOffice.vacation.service.VacationService;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalDto;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFlowDto;
import com.fiveLink.linkOffice.vacationapproval.service.VacationApprovalService;

@Controller
public class VacationApprovalViewController {
	
	private final MemberService memberService;
    private final VacationService vacationService;
    private final VacationApprovalService vacationApprovalService;
	
    @Autowired
    public VacationApprovalViewController(MemberService memberService, VacationService vacationService, VacationApprovalService vacationApprovalService) {
        this.memberService = memberService;
        this.vacationService = vacationService;
        this.vacationApprovalService = vacationApprovalService;
    }
    
 // 사용자 휴가 결재 등록 페이지
 	@GetMapping("/employee/vacationapproval/create")
 	public String employeeVacationApprovalCreate(Model model) {
 		Long member_no = memberService.getLoggedInMemberNo();
 		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
 		List<VacationTypeDto> vacationTypeList = vacationService.selectVacationTypeList();
 		
 		model.addAttribute("memberdto", memberdto);
 		model.addAttribute("vacationTypeList", vacationTypeList);
 		
 		return "employee/vacationapproval/vacationapproval_create";
 	}
 	
 	private Sort getSortOption(String sort) {
		if ("latest".equals(sort)) {
			return Sort.by(Sort.Order.desc("vacationApprovalCreateDate")); 
		} else if ("oldest".equals(sort)) {
			return Sort.by(Sort.Order.asc("vacationApprovalCreateDate")); 
		}
		return Sort.by(Sort.Order.desc("vacationApprovalCreateDate")); 
	}
 	
 	//  사용자 휴가 결재함 페이지
 	@GetMapping("/employee/vacationapproval/list")
 	public String employeevacationapprovalList(Model model, VacationApprovalDto searchdto, @PageableDefault(size = 10, sort = "positionLevel", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(value = "sort", defaultValue = "latest") String sort) {
 		Long member_no = memberService.getLoggedInMemberNo();
 		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
 		
 		Sort sortOption = getSortOption(sort);
		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);
 		
 		Page<VacationApprovalDto> vacationapprovalList = vacationApprovalService.getVacationApprovalByNo(member_no,searchdto,sortedPageable);
 		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
 		vacationapprovalList.forEach(vapp -> {
 			if(vapp.getVacation_approval_create_date() != null) {
 				String fomattedCreateDate = vapp.getVacation_approval_create_date().format(formatter);
 				vapp.setFormat_vacation_approval_create_date(fomattedCreateDate);
 			}
 		});
 		
 		model.addAttribute("memberdto", memberdto);
 		model.addAttribute("vacationapprovalList", vacationapprovalList.getContent());
		model.addAttribute("page", vacationapprovalList);
		model.addAttribute("searchDto", searchdto);
		model.addAttribute("currentSort", sort);
 		
 		return "employee/vacationapproval/vacationapproval_list";
 	}
 	
 	// 사용자 휴가 결재 상세 페이지
 	@GetMapping("/employee/vacationapproval/detail/{vacation_approval_no}")
 	public String employeevacationapprovalDetail(Model model, @PathVariable("vacation_approval_no") Long vacationApprovalNo) {
 		
 		VacationApprovalDto vacationapprovaldto = vacationApprovalService.selectVacationApprovalOne(vacationApprovalNo);
 		
 		Long member_no = memberService.getLoggedInMemberNo();
 		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
 		
 		
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (vacationapprovaldto.getVacation_approval_create_date() != null ) {
            String formattedCreateDate = vacationapprovaldto.getVacation_approval_create_date().format(formatter);
            vacationapprovaldto.setFormat_vacation_approval_create_date(formattedCreateDate);
        }
        
        if (vacationapprovaldto.getFlows() != null) {
            for (VacationApprovalFlowDto flow : vacationapprovaldto.getFlows()) {
                if (flow.getVacation_approval_flow_complete_date() != null) {
                    String formattedCompleteDate = flow.getVacation_approval_flow_complete_date().format(formatter);
                    flow.setFormat_vacation_approval_flow_complete_date(formattedCompleteDate);
                }
            }
        }
 		
 		model.addAttribute("vacationapprovaldto", vacationapprovaldto);
 		model.addAttribute("memberdto", memberdto);
 		
 		return "employee/vacationapproval/vacationapproval_detail";
 	}
 	
 	// 휴가 결재 수정 페이지
 	@GetMapping("/employee/vacationapproval/edit/{vacation_approval_no}")
 	public String employeevacationapprovalEdit(Model model, @PathVariable("vacation_approval_no") Long vacationApprovalNo) {
 		
 		VacationApprovalDto vacationapprovaldto = vacationApprovalService.selectVacationApprovalOne(vacationApprovalNo);
 		
 		Long member_no = memberService.getLoggedInMemberNo();
 		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
 		
 		List<VacationTypeDto> vacationTypeList = vacationService.selectVacationTypeList();
 		
 		model.addAttribute("vacationapprovaldto", vacationapprovaldto);
 		model.addAttribute("memberdto", memberdto);
 		model.addAttribute("vacationTypeList", vacationTypeList);
 		
 		return "employee/vacationapproval/vacationapproval_edit";
 	}
 	
}
