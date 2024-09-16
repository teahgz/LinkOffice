package com.fiveLink.linkOffice.meeting.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT mr FROM MeetingReservation mr " +
            "JOIN Meeting m ON mr.meetingNo = m.meetingNo " +
            "WHERE mr.memberNo = :memberNo " +
            "AND mr.meetingNo = :meetingNo " +
            "AND mr.meetingReservationPurpose LIKE %:searchText% " +
            "AND mr.meetingReservationStatus = 0")
     Page<MeetingReservation> findByMemberNoAndMeetingNoAndMeetingReservationPurposeContaining(@Param("memberNo") Long memberNo,@Param("meetingNo") Long meetingNo, @Param("searchText") String searchText, Pageable pageable);

     Page<MeetingReservation> findByMemberNoAndMeetingReservationPurposeContaining(Long memberNo, String searchText, Pageable pageable);

} 