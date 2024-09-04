package com.fiveLink.linkOffice.attendance.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.attendance.domain.Attendance;
import com.fiveLink.linkOffice.attendance.domain.AttendanceDto;
import com.fiveLink.linkOffice.attendance.repository.AttendanceRepository;

@Service
public class AttendanceService {

	private final AttendanceRepository attendanceRepository;
	
	@Autowired
	public AttendanceService(AttendanceRepository attendanceRepository) {
		this.attendanceRepository = attendanceRepository;
	}
	// 근태 조회 리스트 
	public List<AttendanceDto> selectAttendanceList(Long memberNo){
		List<Attendance> attendanceList = attendanceRepository.findByMemberNo(memberNo);
		
		List<AttendanceDto> attendanceDtoList = new ArrayList<AttendanceDto>();
		
		for(Attendance a : attendanceList) {
			AttendanceDto attendanceDto = new AttendanceDto().toDto(a);
			attendanceDtoList.add(attendanceDto);
		}
		return attendanceDtoList;
	}
	// 출근 여부 조회
	public AttendanceDto findByMemberNoAndWorkDate(Long memberNo, LocalDate today) {
		Attendance attendance = attendanceRepository.findByMemberNoAndWorkDate(memberNo, today);
		if(attendance != null) {
			return new AttendanceDto(	
				attendance.getAttendanceNo(),
				attendance.getMemberNo(),
				attendance.getWorkDate(),
				attendance.getCheckInTime(),
				attendance.getCheckOutTime()
			);
		} else {
			return null;			
		}
	
	}
	// 출근 기능 
	public int attendanceCheckIn(Attendance attendance) {
		int result = -1;
		try {
			// 출근 기능이 잘 동작하면 
			attendanceRepository.save(attendance);
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	// 퇴근 기능
	public int attendanceCheckOut(Attendance attendance) {
		int result = -1; 
		try {
			attendanceRepository.save(attendance);
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
}
