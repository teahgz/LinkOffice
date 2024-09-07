package com.fiveLink.linkOffice.attendance.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fiveLink.linkOffice.attendance.domain.Attendance;
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
   
   // 모든 근태 조회 리스트(달력에 쓰일 목적) 
   @GetMapping("/employee/attendance/myAttendance/{member_no}")
   public String documentPersonalPage(Model model,
                                      @PathVariable("member_no") Long memberNo) {
       // 기본 페이지를 0으로 설정
       int page = 0;
  
       List<AttendanceDto> attendanceList = attendanceService.selectAttendanceList(memberNo);
       // memberDto 불러오기
       List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);

       model.addAttribute("memberdto", memberdto);
       model.addAttribute("attendanceList", attendanceList);
       
       return "employee/attendance/myAttendance";
   }
   // 근태 조회 리스트(페이징) 
   @GetMapping("/employee/attendance/myAttendance")
   @ResponseBody
   public ResponseEntity<?> getAttendanceList(@RequestParam("member_no") Long memberNo,
                                              @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                              @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
       List<Attendance> attendanceList = attendanceService.findAttendanceList(memberNo, startDate, endDate);
       return ResponseEntity.ok(attendanceList);
   }


}