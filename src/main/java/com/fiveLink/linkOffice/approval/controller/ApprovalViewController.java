package com.fiveLink.linkOffice.approval.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

import com.fiveLink.linkOffice.approval.domain.ApprovalFormDto;
import com.fiveLink.linkOffice.approval.service.ApprovalFormService;
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApproval;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalDto;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFlowDto;
import com.fiveLink.linkOffice.vacationapproval.service.VacationApprovalService;

@Controller
public class ApprovalViewController {

	private final MemberService memberService;
	private final ApprovalFormService approvalFormService;
	private final VacationApprovalService vacationApprovalService;

	@Autowired
	public ApprovalViewController(MemberService memberService, ApprovalFormService approvalFormService,
			VacationApprovalService vacationApprovalService) {
		this.memberService = memberService;
		this.approvalFormService = approvalFormService;
		this.vacationApprovalService = vacationApprovalService;
	}

	// 관리자 전자결재 양식 등록 페이지
	@GetMapping("/admin/approval/create")
	public String adminApprovalCreate(Model model) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);

		model.addAttribute("memberdto", memberdto);

		return "admin/approval/approval_create";
	}

	private Sort getSortOption(String sort) {
		if ("latest".equals(sort)) {
			return Sort.by(Sort.Order.desc("approvalFormCreateDate"));
		} else if ("oldest".equals(sort)) {
			return Sort.by(Sort.Order.asc("approvalFormCreateDate"));
		}
		return Sort.by(Sort.Order.desc("approvalFormCreateDate"));
	}

	// 관리자 전자결재 양식함 페이지
	@GetMapping("/admin/approval/form")
	public String adminApprovalForm(Model model, ApprovalFormDto searchdto,
			@PageableDefault(size = 10, sort = "positionLevel", direction = Sort.Direction.DESC) Pageable pageable,
			@RequestParam(value = "sort", defaultValue = "latest") String sort) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);

		Sort sortOption = getSortOption(sort);
		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);

		Page<ApprovalFormDto> formList = approvalFormService.getAllApprovalForms(sortedPageable, searchdto);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		formList.forEach(form -> {
			if (form.getApproval_form_create_date() != null) {
				String fomattedCreateDate = form.getApproval_form_create_date().format(formatter);
				form.setFormat_create_date(fomattedCreateDate);
			}
		});

		model.addAttribute("memberdto", memberdto);
		model.addAttribute("formList", formList.getContent());
		model.addAttribute("page", formList);
		model.addAttribute("searchDto", searchdto);
		model.addAttribute("currentSort", sort);

		return "admin/approval/approval_form";
	}

	// 관리자 전자결재 양식 상세 페이지
	@GetMapping("/admin/approval/detail/{form_no}")
	public String adminApprovalDetail(Model model, @PathVariable("form_no") Long formNo) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);

		ApprovalFormDto formList = approvalFormService.getApprovalFormOne(formNo);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		if (formList.getApproval_form_create_date() != null) {
			String formattedCreateDate = formList.getApproval_form_create_date().format(formatter);
			formList.setFormat_create_date(formattedCreateDate);
		}

		model.addAttribute("memberdto", memberdto);
		model.addAttribute("formList", formList);
		return "admin/approval/approval_detail";
	}

	// 관리자 전자결재 양식 수정 페이지
	@GetMapping("/admin/approval/edit/{form_no}")
	public String adminApprovalEdit(Model model, @PathVariable("form_no") Long formNo) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);

		ApprovalFormDto formList = approvalFormService.getApprovalFormOne(formNo);

		model.addAttribute("memberdto", memberdto);
		model.addAttribute("formList", formList);

		return "admin/approval/approval_edit";
	}

	private Sort getSortOptionApproval(String sort) {
		if ("latest".equals(sort)) {
			return Sort.by(Sort.Order.desc("vacationApprovalCreateDate"));
		} else if ("oldest".equals(sort)) {
			return Sort.by(Sort.Order.asc("vacationApprovalCreateDate"));
		}
		return Sort.by(Sort.Order.desc("vacationApprovalCreateDate"));
	}

	// 사용자 전자결재 내역함
	@GetMapping("/employee/approval/history")
	public String approvalHistory(Model model, VacationApprovalDto searchdto,
			@PageableDefault(size = 10, sort = "positionLevel", direction = Sort.Direction.DESC) Pageable pageable,
			@RequestParam(value = "sort", defaultValue = "latest") String sort) {

		Long memberNo = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);

		Sort sortOption = getSortOptionApproval(sort);
		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);

		// 결재흐름에 번호가 있는지 조회
		List<VacationApprovalFlowDto> vacationApprovalFlowDtos = vacationApprovalService
				.getVacationApprovalFlowRoleByMemberNo(memberNo);

		List<Long> vacationApprovalNos = vacationApprovalFlowDtos.stream()
				.map(VacationApprovalFlowDto::getVacation_approval_no).distinct().collect(Collectors.toList());

		// 문서 번호 조회
		Page<VacationApprovalDto> vacationApprovalDtoPage = vacationApprovalService
				.getVacationApprovalsByNo(vacationApprovalNos, searchdto, sortedPageable);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		vacationApprovalDtoPage.getContent().forEach(vapp -> {
			if (vapp.getVacation_approval_create_date() != null) {
				String formattedCreateDate = vapp.getVacation_approval_create_date().format(formatter);
				vapp.setFormat_vacation_approval_create_date(formattedCreateDate);
			}
		});
		System.out.println(vacationApprovalDtoPage.getContent());
		
		model.addAttribute("memberdto", memberdto);
		model.addAttribute("vacationApprovalDtoList", vacationApprovalDtoPage.getContent());
		model.addAttribute("page", vacationApprovalDtoPage);
		model.addAttribute("searchDto", searchdto);
		model.addAttribute("currentSort", sort);

		return "employee/approval/approval_history_list";

	}

	
	 // 사용자 결재 참조함 페이지
	 
	 @GetMapping("/employee/approval/references") public String approvalReferences(Model model, VacationApprovalDto searchdto,
				@PageableDefault(size = 10, sort = "positionLevel", direction = Sort.Direction.DESC) Pageable pageable,
				@RequestParam(value = "sort", defaultValue = "latest") String sort) { 
		 Long memberNo =memberService.getLoggedInMemberNo(); 
		 List<MemberDto> memberdto =  memberService.getMembersByNo(memberNo);

			Sort sortOption = getSortOptionApproval(sort);
			Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);

			// 결재흐름에 번호가 있는지 조회
			List<VacationApprovalFlowDto> vacationApprovalFlowDtos = vacationApprovalService
					.getVacationApprovalFlowByMemberNo(memberNo);

			List<Long> vacationApprovalNos = vacationApprovalFlowDtos.stream()
					.map(VacationApprovalFlowDto::getVacation_approval_no).distinct().collect(Collectors.toList());

			// 문서 번호 조회
			Page<VacationApprovalDto> vacationApprovalDtoPage = vacationApprovalService
					.getVacationApprovalsByNo(vacationApprovalNos, searchdto, sortedPageable);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			vacationApprovalDtoPage.getContent().forEach(vapp -> {
				if (vapp.getVacation_approval_create_date() != null) {
					String formattedCreateDate = vapp.getVacation_approval_create_date().format(formatter);
					vapp.setFormat_vacation_approval_create_date(formattedCreateDate);
				}
			});		 
		 
			model.addAttribute("memberdto", memberdto);
			model.addAttribute("vacationApprovalDtoList", vacationApprovalDtoPage.getContent());
			model.addAttribute("page", vacationApprovalDtoPage);
			model.addAttribute("searchDto", searchdto);
			model.addAttribute("currentSort", sort);
		 
		 return "employee/approval/approval_references_list"; 
	}

	// 사용자 결재 진행함 페이지
	@GetMapping("/employee/approval/progress")
	public String approvalProgress(Model model) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);

		model.addAttribute("memberdto", memberdto);

		return "employee/approval/approval_progress_list";
	}

	// 사용자 결재 반려함 페이지
	@GetMapping("/employee/approval/reject")
	public String approvalReject(Model model) {
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);

		model.addAttribute("memberdto", memberdto);

		return "employee/approval/approval_reject_list";
	}
	
	// 사용자 결재 내역함 상세 페이지 
	@GetMapping("/employee/approval/approval_history_detail/{vacationapproval_no}")
	public String approvalHistoryDetail(Model model, @PathVariable("vacationapproval_no") Long vacationApprovalNo) {
		
		Long member_no = memberService.getLoggedInMemberNo();
		List<MemberDto> memberdto = memberService.getMembersByNo(member_no);
		
		VacationApprovalDto vacationapprovaldto = vacationApprovalService.selectVacationApprovalOne(vacationApprovalNo);
		
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
	                
	                MemberDto currentMember = memberService.selectMemberOne(flow.getMember_no());
	                flow.setDigital_name(currentMember.getMember_new_digital_img());
	                
	            }
	        }
	 		
	        
	        System.out.println(vacationapprovaldto);
		model.addAttribute("memberdto", memberdto);
		model.addAttribute("vacationapprovaldto", vacationapprovaldto);
		model.addAttribute("currentUserMemberNo", member_no);
		
		return "employee/approval/approval_history_detail";
	}
	
}
