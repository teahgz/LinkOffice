package com.fiveLink.linkOffice.schedule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.schedule.domain.Schedule;
import com.fiveLink.linkOffice.schedule.domain.ScheduleRepeat; 

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
	long countByScheduleCategoryNoAndScheduleStatus(Long scheduleCategoryNo, Long scheduleStatus);
	
	@Query("SELECT s FROM Schedule s WHERE s.scheduleType = :scheduleType AND s.scheduleStatus = :scheduleStatus")
	List<Schedule> findByScheduleTypeAndScheduleStatus(@Param("scheduleType") Long scheduleType, @Param("scheduleStatus") Long scheduleStatus);
	
//	// 관리자 - 반복 일정 수정
//	List<Schedule> findAllByScheduleNo(Long scheduleNo);
//	
//	// 관리자 - 반복 일정 수정
//	@Query("SELECT s FROM Schedule s WHERE s.startDate >= :newStartDate AND s.id != :eventId")
//    List<Schedule> findFutureEvents(Long eventId, String newStartDate);
 
}
