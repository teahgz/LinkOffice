package com.fiveLink.linkOffice.meeting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.meeting.domain.MeetingReservation;

@Repository
public interface MeetingReservationRepository extends JpaRepository<MeetingReservation, Long> {
	@Query("SELECT mr, mem, pos.positionName, dept.departmentName " +
		       "FROM MeetingReservation mr " +
		       "JOIN Meeting m ON mr.meetingNo = m.meetingNo " +
		       "JOIN Member mem ON mr.memberNo = mem.memberNo " +
		       "JOIN Position pos ON mem.positionNo = pos.positionNo " +    
		       "JOIN Department dept ON mem.departmentNo = dept.departmentNo " +  
		       "WHERE mr.meetingReservationDate = :date " +
		       "AND mr.meetingReservationStatus = :status " +
		       "ORDER BY mr.meetingReservationStartTime ASC")
    List<Object[]> findReservations(@Param("date") String date, @Param("status") Long status);
} 