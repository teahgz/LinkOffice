package com.fiveLink.linkOffice.schedule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.organization.domain.Department;
import com.fiveLink.linkOffice.schedule.domain.ScheduleCategory;

@Repository
public interface ScheduleCategoryRepository extends JpaRepository<ScheduleCategory, Long>{
    List<ScheduleCategory> findAllByScheduleCategoryStatusOrderByScheduleCategoryNameAsc(Long scheduleCategoryStatus);
    
}
