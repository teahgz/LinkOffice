package com.fiveLink.linkOffice.meeting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiveLink.linkOffice.meeting.domain.Meeting;
 
public interface MeetingRepository extends JpaRepository<Meeting, Long>{
	
	List<Meeting> findByMeetingStatus(long meetingStatus);
	
	// 회의실명 증복 확인
	boolean existsByMeetingNameAndMeetingStatus(String meetingName, long meetingStatus);

}
