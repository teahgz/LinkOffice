package com.fiveLink.linkOffice.schedule.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.organization.domain.DepartmentDto;
import com.fiveLink.linkOffice.schedule.domain.ScheduleCategoryDto;
import com.fiveLink.linkOffice.schedule.service.ScheduleCategoryService;

@Controller
public class ScheduleController { 
	private final MemberService memberService; 
	private final ScheduleCategoryService scheduleCategoryService;
	
	@Autowired
	public ScheduleController(MemberService memberService, ScheduleCategoryService scheduleCategoryService) { 
	    this.memberService = memberService; 
	    this.scheduleCategoryService = scheduleCategoryService; 
	}
    @GetMapping("/schedule/category")
    public String listPermissions(Model model) { 
        Long memberNo = memberService.getLoggedInMemberNo();  
        List<MemberDto> memberDto = memberService.getMembersByNo(memberNo);
        List<ScheduleCategoryDto> scheduleCategories = scheduleCategoryService.getAllScheduleCategories();
        
        System.out.println(scheduleCategories);
        model.addAttribute("memberdto", memberDto); 
        model.addAttribute("scheduleCategories", scheduleCategories);
        
        return "/admin/schedule/scheduleCategory";
    }
}
