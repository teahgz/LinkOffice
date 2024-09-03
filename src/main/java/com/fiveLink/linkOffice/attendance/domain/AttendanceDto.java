package com.fiveLink.linkOffice.attendance.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fiveLink.linkOffice.document.domain.DocumentFolder;
import com.fiveLink.linkOffice.document.domain.DocumentFolderDto;
import com.fiveLink.linkOffice.member.domain.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class AttendanceDto {
	private Long attendance_no;
	private Long member_no;
	private LocalDate work_date;
	private LocalTime check_in_time;
	private LocalTime check_out_time;
	
	public Attendance toEntity(Attendance attendance) {
        return Attendance.builder()
                .attendanceNo(attendance_no)
                .memberNo(member_no)
                .workDate(work_date)
                .checkInTime(check_in_time)
                .checkOutTime(check_out_time)
                .build();
    }
	
	public static AttendanceDto toDto(Attendance attendance) {
        return AttendanceDto.builder()
                .attendance_no(attendance.getAttendanceNo())
                .member_no(attendance.getMemberNo())
                .work_date(attendance.getWorkDate())
                .check_in_time(attendance.getCheckInTime())
                .check_out_time(attendance.getCheckOutTime())
                .build();
    }
}
