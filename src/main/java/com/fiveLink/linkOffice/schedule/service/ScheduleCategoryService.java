package com.fiveLink.linkOffice.schedule.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.schedule.domain.ScheduleCategory;
import com.fiveLink.linkOffice.schedule.domain.ScheduleCategoryDto;
import com.fiveLink.linkOffice.schedule.repository.ScheduleCategoryRepository;
import com.fiveLink.linkOffice.schedule.repository.ScheduleRepository;

import jakarta.transaction.Transactional;

@Service
public class ScheduleCategoryService {
    private final ScheduleCategoryRepository scheduleCategoryRepository;
    private final ScheduleRepository scheduleRepository;
    
    @Autowired
    public ScheduleCategoryService(ScheduleCategoryRepository scheduleCategoryRepository, ScheduleRepository scheduleRepository) {
        this.scheduleCategoryRepository = scheduleCategoryRepository;
        this.scheduleRepository = scheduleRepository;
    }
  
    public List<ScheduleCategoryDto> getAllScheduleCategories() {
        List<ScheduleCategory> scheduleCategories = scheduleCategoryRepository.findAllByScheduleCategoryStatusOrderByScheduleCategoryNameAsc(0L);
        return scheduleCategories.stream()
                .map(ScheduleCategoryDto::toDto) 
                .collect(Collectors.toList());
    }
     
    public ScheduleCategoryDto getScheduleCategoryById(Long id) {
        return scheduleCategoryRepository.findById(id)
                .map(ScheduleCategoryDto::toDto)   
                .orElse(null);  
    }
    
    // 수정
    public void updateScheduleCategory(Long categoryId, String categoryName, String categoryColor, Long onlyAdmin) {
        ScheduleCategory category = scheduleCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));
        
        category.setScheduleCategoryName(categoryName);
        category.setScheduleCategoryColor(categoryColor);
        category.setScheduleCategoryAdmin(onlyAdmin);
        
        scheduleCategoryRepository.save(category);
    }
    
    // 삭제
    @Transactional
    public boolean deleteScheduleCategory(Long categoryId) {
        Optional<ScheduleCategory> categoryOpt = scheduleCategoryRepository.findById(categoryId);

        if (categoryOpt.isPresent()) {
            ScheduleCategory category = categoryOpt.get();
 
            long relatedSchedules = scheduleRepository.countByScheduleCategoryNoAndScheduleStatus(categoryId, 0L);

            // 카테고리 사용 중이면 삭제 불가
            if (relatedSchedules > 0) { 
                return false;
            } else { 
                category.setScheduleCategoryStatus(1L);
                scheduleCategoryRepository.save(category);
                return true;
            }
        }
        return false;
    }


}
