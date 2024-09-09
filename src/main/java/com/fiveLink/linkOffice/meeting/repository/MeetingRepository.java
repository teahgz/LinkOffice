package com.fiveLink.linkOffice.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiveLink.linkOffice.meeting.domain.Meeting;
 
public interface MeetingRepository extends JpaRepository<Meeting, Long>{

}
