package com.fiveLink.linkOffice.attendance.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.fiveLink.linkOffice.attendance.domain.AttendanceDto;
import com.fiveLink.linkOffice.attendance.service.AttendanceService;
import com.fiveLink.linkOffice.document.controller.DocumentViewController;

@Controller
public class AttendanceViewController {

	private final AttendanceService attendanceService;
	
	private static final Logger LOGGER
	= LoggerFactory.getLogger(AttendanceViewController.class);
	
	@Autowired
	public AttendanceViewController(AttendanceService attendanceService) {
		this.attendanceService = attendanceService;
	}
	// 근태 조회 
	@GetMapping("/employee/attendance/myAttendance/{member_no}")
	public String documentPersonalPage(Model model,
			@PathVariable("member_no") Long memberNo
			) {
		List<AttendanceDto> attendanceList = attendanceService.selectAttendanceList(memberNo);
		LOGGER.debug("attendance List: {}", attendanceList);
		model.addAttribute("attendanceList", attendanceList);
		return "employee/attendance/myAttendance";
	}
}
