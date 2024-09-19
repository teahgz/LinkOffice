package com.fiveLink.linkOffice.schedule.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.schedule.domain.ScheduleCategory;
import com.fiveLink.linkOffice.schedule.domain.ScheduleCategoryDto;
import com.fiveLink.linkOffice.schedule.repository.ScheduleCategoryRepository;

@Service
public class ScheduleCategoryService {
    private final ScheduleCategoryRepository scheduleCategoryRepository;

    @Autowired
    public ScheduleCategoryService(ScheduleCategoryRepository scheduleCategoryRepository) {
        this.scheduleCategoryRepository = scheduleCategoryRepository;
    }
 
    public List<ScheduleCategoryDto> getAllScheduleCategories() {
        List<ScheduleCategory> scheduleCategories = scheduleCategoryRepository
                .findAllByScheduleCategoryStatusOrderByScheduleCategoryNameAsc(0L);
        return scheduleCategories.stream()
                .map(ScheduleCategoryDto::toDto)   
                .collect(Collectors.toList());
    }
}
