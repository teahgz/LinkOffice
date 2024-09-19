package com.fiveLink.linkOffice.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.schedule.domain.ScheduleException; 
@Repository
public interface ScheduleExceptionRepository extends JpaRepository<ScheduleException, Long>{

}
