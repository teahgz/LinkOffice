package com.fiveLink.linkOffice.organization.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fiveLink.linkOffice.organization.domain.DepartmentDto;
import com.fiveLink.linkOffice.organization.service.DepartmentService; 

@Controller
public class DepartmentViewController {
	 
	private final DepartmentService departmentService;
	
	private static final Logger LOGGER 
	= LoggerFactory.getLogger(DepartmentViewController.class);
	
	@Autowired
	public DepartmentViewController(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}
	
	@GetMapping("/department")
	public String selectBoardList(Model model) {   
		List<DepartmentDto> departments = departmentService.selectBoardList();
		model.addAttribute("departments", departments);   
		return "/admin/organization/department_list";
	}
}
