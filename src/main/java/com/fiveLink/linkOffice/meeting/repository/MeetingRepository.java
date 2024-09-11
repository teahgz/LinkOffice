package com.fiveLink.linkOffice.meeting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiveLink.linkOffice.meeting.domain.Meeting;
import com.fiveLink.linkOffice.meeting.domain.MeetingDto;
 
public interface MeetingRepository extends JpaRepository<Meeting, Long>{
	
	List<Meeting> findByMeetingStatusOrderByMeetingNameAsc(long meetingStatus);
	
	// 회의실명 증복 확인
	boolean existsByMeetingNameAndMeetingStatus(String meetingName, long meetingStatus);
	
	boolean existsByMeetingNameAndMeetingStatusAndMeetingNoNot(String meetingName, long meetingStatus, long meetingNo);
	
	List<Meeting> findByMeetingNameContainingIgnoreCaseAndMeetingStatusOrderByMeetingNameAsc(String meetingName, long meetingStatus);
}
