package com.fiveLink.linkOffice.attendance.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.attendance.domain.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>{

	List<Attendance> findByMemberNo(Long memberNo);
	
	Attendance findByMemberNoAndWorkDate(Long memberNo, LocalDate today);
}
