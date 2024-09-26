package com.fiveLink.linkOffice.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.schedule.domain.ScheduleExceptionParticipant;

@Repository
public interface ScheduleExceptionParticipantRepository extends JpaRepository<ScheduleExceptionParticipant, Long>{

}
