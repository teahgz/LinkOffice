package com.fiveLink.linkOffice.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.meeting.domain.MeetingParticipant;

@Repository
public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {

}

