package com.fiveLink.linkOffice.attendance.service;

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
	
	public List<AttendanceDto> selectAttendanceList(Long memberNo){
		List<Attendance> attendanceList = attendanceRepository.findByMemberNo(memberNo);
		
		List<AttendanceDto> attendanceDtoList = new ArrayList<AttendanceDto>();
		
		for(Attendance a : attendanceList) {
			AttendanceDto attendanceDto = new AttendanceDto().toDto(a);
			attendanceDtoList.add(attendanceDto);
		}
		return attendanceDtoList;
	}
}
