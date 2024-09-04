package com.fiveLink.linkOffice.attendance.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.attendance.domain.Attendance;
import com.fiveLink.linkOffice.attendance.domain.AttendanceDto;
import com.fiveLink.linkOffice.attendance.repository.AttendanceRepository;
import com.fiveLink.linkOffice.document.domain.DocumentFolder;
import com.fiveLink.linkOffice.document.domain.DocumentFolderDto;

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
	public int attendanceCheckOut(Long memberNo, LocalDate today) {
		int result = -1; 
		try {
			attendanceRepository.findByMemberNoAndWorkDate(memberNo, today);
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
}
