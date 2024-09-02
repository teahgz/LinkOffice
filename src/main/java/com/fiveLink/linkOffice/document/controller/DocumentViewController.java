package com.fiveLink.linkOffice.document.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fiveLink.linkOffice.document.domain.DocumentFolderDto;
import com.fiveLink.linkOffice.document.service.DocumentFolderService;

@Controller
public class DocumentViewController {
	
	private final DocumentFolderService documentFolderService;
	
	private static final Logger LOGGER
		= LoggerFactory.getLogger(DocumentViewController.class);
	
	@Autowired
	public DocumentViewController(DocumentFolderService documentFolderService) {
		this.documentFolderService = documentFolderService;
	}
	// 개인 문서함 : 사원번호를 받아옴 
	@GetMapping("/document/{member_no}")
	public String documentPersonalPage(Model model,
			@PathVariable("member_no") Long member_no) {
		List<DocumentFolderDto> folderDto = documentFolderService.selectPersonalFolderList(member_no);
		model.addAttribute("folderDto", folderDto);
		return "document/personal";
	}
	// 부서 문서함 : 부서 번호를 받아옴 
	@GetMapping("/document/{department_no}")
	public String documentDepartmentPage(Model model,
			@PathVariable("department_no") Long department_no) {
		List<DocumentFolderDto> folderDto = documentFolderService.selectDepartmentFolderList(department_no);
		model.addAttribute("folderDto", folderDto);
		return "document/department";
	}
	// 사내 문서함 : 문서함 타입 = 2 로 지정해서 service에 보내줌 
	@GetMapping("/document/company")
	public String documentCompanyPage(Model model) {
		Long documentBoxType = 2L;
		List<DocumentFolderDto> folderDto = documentFolderService.selectCompanyFolderList(documentBoxType);
		model.addAttribute("folderDto", folderDto);
		return "document/company";
	}
}
