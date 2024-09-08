package com.fiveLink.linkOffice.attendance.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
   
   // 모든 근태 조회 리스트(달력에 쓰일 목적), 이번 달 근태별 count 
   @GetMapping("/employee/attendance/myAttendance/{member_no}")
   public String documentPersonalPage(Model model,
                                      @PathVariable("member_no") Long memberNo) {
	   LocalDate today = LocalDate.now();
	   LocalDate startDate = today.withDayOfMonth(1);
	   LocalDate lastDate = today.withDayOfMonth(today.lengthOfMonth());
       List<AttendanceDto> attendanceList = attendanceService.selectAttendanceList(memberNo);
       List<AttendanceDto> thisMonthAttendanceList = attendanceService.findAttendanceList(memberNo, startDate, lastDate);
       
       long countAttendance = thisMonthAttendanceList.size();
       // 결근 
       long countAbsence = thisMonthAttendanceList.stream()
               .filter(attendance -> attendance.getCheck_in_time() == null)
               .count();

       // 지각 
       long countLate = thisMonthAttendanceList.stream()
               .filter(attendance -> attendance.getCheck_in_time() != null)
               .filter(attendance -> attendance.getCheck_in_time().getHour() >= 9)
               .count();
       
       // memberDto 불러오기
       List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);

       model.addAttribute("memberdto", memberdto);
       model.addAttribute("attendanceList", attendanceList);
       model.addAttribute("countAttendance", countAttendance);
       model.addAttribute("countAbsence", countAbsence);
       model.addAttribute("countLate", countLate);
       
       return "employee/attendance/myAttendance";
   }
   
   // 근태 조회 리스트(페이징) 
   @GetMapping("/employee/attendance/myAttendance")
   @ResponseBody
   public ResponseEntity<?> getAttendanceList(@RequestParam("member_no") Long memberNo,
                                              @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                              @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
       List<AttendanceDto> attendanceList = attendanceService.findAttendanceList(memberNo, startDate, endDate);
       return ResponseEntity.ok(attendanceList);
   }
   
   // 공휴일 조회
   @GetMapping("/holidays")
   public ResponseEntity<Map<String, Object>> getHolidays(@RequestParam("year") String year,
                                                           @RequestParam("month") String month) throws IOException {
       Map<String, Object> holidayData = attendanceService.holidayInfoAPI(year, month);
       return ResponseEntity.ok(holidayData);
   }

}