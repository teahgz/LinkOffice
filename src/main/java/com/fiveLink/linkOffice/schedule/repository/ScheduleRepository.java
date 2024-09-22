package com.fiveLink.linkOffice.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.schedule.domain.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
	long countByScheduleCategoryNoAndScheduleStatus(Long scheduleCategoryNo, Long scheduleStatus);
}
