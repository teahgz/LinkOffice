package com.fiveLink.linkOffice.attendance.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fiveLink.linkOffice.attendance.domain.Attendance;
import com.fiveLink.linkOffice.attendance.service.AttendanceService;

@Controller
public class AttendanceApiController {

	@Autowired
	private AttendanceService attendanceService;
	
	@PostMapping("/attendance/checkIn")
	@ResponseBody
	public Map<String, String> checkIn(@RequestBody Map<String, Long> payload){
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "404");
        resultMap.put("res_msg", "출석 기록 중 오류가 발생했습니다.");

        Long memberNo = payload.get("memberNo");
        // 오늘 날짜와 시간 
        LocalDate today = LocalDate.now();
        LocalTime time = LocalTime.now();
        
        // Attendance Entity로 build 
        Attendance attendance = Attendance.builder()
        		.memberNo(memberNo)
        		.workDate(today)
        		.checkInTime(time)
        		.build();

        // build한 Entity를 service로 넘겨줘서 return이 1이면 성공
        if (attendanceService.attendanceCheckIn(attendance) > 0) {
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "출근 확인되었습니다.");
        }
        return resultMap;
    }
	
//	@PostMapping("/attendance/checkOut")
//	@ResponseBody
//	public Map<String, String> checkOut(@RequestBody Map<String, Long> payload){
//        Map<String, String> resultMap = new HashMap<>();
//        resultMap.put("res_code", "404");
//        resultMap.put("res_msg", "출석 기록 중 오류가 발생했습니다.");
//
//        Long memberNo = payload.get("memberNo");
//        // 오늘 날짜와 시간 
//        LocalDate today = LocalDate.now();
//        LocalTime time = LocalTime.now();
//        
//        // 오늘 출근을 했는지 DB에서 조회
//        if(attendanceService.attendanceCheckOut(memberNo, today) > 0) {
//        	// 조회가 정상적으로 되면 
//        	// Attendance Entity로 build 
//        	Attendance attendance = Attendance.builder()
//        			.memberNo(memberNo)
//        			.workDate(today)
//        			.checkOutTime(time)
//        			.build();
//        	
//        	// build한 Entity를 service로 넘겨줘서 return이 1이면 성공
//        	if (attendanceService.attendanceCheckOut(attendance) > 0) {
//        		resultMap.put("res_code", "200");
//        		resultMap.put("res_msg", "출근 확인되었습니다.");
//        	}        	
//        }
//        
//        return resultMap;
//    }
}
