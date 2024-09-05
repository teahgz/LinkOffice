package com.fiveLink.linkOffice.attendance.controller;

import java.io.IOException;
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
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;

@Controller
public class AttendanceViewController {

   private final AttendanceService attendanceService;
   
   private final MemberService memberService;
   
   private static final Logger LOGGER
   = LoggerFactory.getLogger(AttendanceViewController.class);
   
   @Autowired
   public AttendanceViewController(AttendanceService attendanceService, MemberService memberService) {
      this.attendanceService = attendanceService;
      this.memberService = memberService; 
   }
   
   // 근태 조회 
   @GetMapping("/employee/attendance/myAttendance/{member_no}")
   public String documentPersonalPage(Model model,
         @PathVariable("member_no") Long memberNo)  {
      List<AttendanceDto> attendanceList = attendanceService.selectAttendanceList(memberNo);
      LOGGER.debug("attendance List: {}", attendanceList);
      
      // memberDto 불러오기 
      List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);
      
      model.addAttribute("memberdto", memberdto);
      model.addAttribute("attendanceList", attendanceList);
      return "employee/attendance/myAttendance";
   }

}
