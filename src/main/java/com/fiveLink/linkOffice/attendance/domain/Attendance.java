package com.fiveLink.linkOffice.attendance.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "fl_attendance")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Attendance {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_no")
    private Long attendanceNo;
	
	@Column(name = "member_no")
    private Long memberNo;
	
	@Column(name = "work_date")
    private LocalDate workDate;

	@Column(name = "check_in_time")
	private LocalTime checkInTime;
	
	@Column(name = "check_out_time")
	private LocalTime checkOutTime;
}
