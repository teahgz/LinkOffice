package com.fiveLink.linkOffice.schedule.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.schedule.domain.Schedule;
import com.fiveLink.linkOffice.schedule.domain.ScheduleDto;
import com.fiveLink.linkOffice.schedule.domain.ScheduleRepeat;
import com.fiveLink.linkOffice.schedule.domain.ScheduleRepeatDto;
import com.fiveLink.linkOffice.schedule.repository.ScheduleRepeatRepository;
import com.fiveLink.linkOffice.schedule.repository.ScheduleRepository;

@Service
public class ScheduleService { 
    private final ScheduleRepository scheduleRepository; 
    private final ScheduleRepeatRepository scheduleRepeatRepository;

	@Autowired
	public ScheduleService(ScheduleRepository scheduleRepository, ScheduleRepeatRepository scheduleRepeatRepository) { 
	    this.scheduleRepository = scheduleRepository; 
	    this.scheduleRepeatRepository = scheduleRepeatRepository;  
	} 

	
	public void saveCompanySchedule(ScheduleDto scheduleDto, ScheduleRepeatDto scheduleRepeatDto) {
	    Schedule schedule = Schedule.builder()
	    		.memberNo(scheduleDto.getMember_no())
	            .scheduleTitle(scheduleDto.getSchedule_title())
	            .scheduleComment(scheduleDto.getSchedule_comment())
	            .scheduleStartDate(scheduleDto.getSchedule_start_date())
	            .scheduleAllday(scheduleDto.getSchedule_allday())
	            .scheduleEndDate(scheduleDto.getSchedule_end_date())
	            .scheduleStartTime(scheduleDto.getSchedule_start_time())
	            .scheduleEndTime(scheduleDto.getSchedule_end_time())
	            .scheduleCategoryNo(scheduleDto.getSchedule_category_no())
	            .scheduleRepeat(scheduleDto.getSchedule_repeat())
	            .scheduleType(3L)
	            .scheduleStatus(0L)
	            .build();

	    scheduleRepository.save(schedule);

	    // 반복 일정 저장
	    if (scheduleDto.getSchedule_repeat() != 0) {
	        ScheduleRepeat repeat = ScheduleRepeat.builder()
	                .scheduleNo(schedule.getScheduleNo())
	                .scheduleRepeatType(scheduleRepeatDto.getSchedule_repeat_type())
	                .scheduleRepeatDay(determineRepeatDay(scheduleRepeatDto.getSchedule_repeat_type(), scheduleRepeatDto.getSchedule_repeat_day())) // 요일
	                .scheduleRepeatWeek(determineRepeatWeek(scheduleRepeatDto.getSchedule_repeat_type(), scheduleRepeatDto.getSchedule_repeat_week())) // 주차
	                .scheduleRepeatDate(determineRepeatDate(scheduleRepeatDto.getSchedule_repeat_type(), scheduleRepeatDto.getSchedule_repeat_date())) // 일자
	                .scheduleRepeatMonth(determineRepeatMonth(scheduleRepeatDto.getSchedule_repeat_type(), scheduleRepeatDto.getSchedule_repeat_month())) // 월
	                .scheduleRepeatEndDate(scheduleRepeatDto.getSchedule_repeat_end_date()) 
	                .build();

	        scheduleRepeatRepository.save(repeat);
	    }
	}

	private Long determineRepeatDay(long repeatOption, Long dayOfWeek) {
	    // 2 (매주) - 요일  
	    if ((repeatOption == 2 || repeatOption == 4) && dayOfWeek != null) {
	        return dayOfWeek; 
	    }
	    return null; 
	}

	private Long determineRepeatWeek(long repeatOption, Long week) {
	    // 4 (매월 주)- 주차 
	    if (repeatOption == 4 && week != null) {
	        return week;  
	    }
	    return null;  
	}

	private Long determineRepeatDate(long repeatOption, Long date) {
	    // 3 (매월 일), 5 (매년)- 일  
	    if ((repeatOption == 3 || repeatOption == 5) && date != null) {
	        return date;  
	    }
	    return null; 
	}

	private Long determineRepeatMonth(long repeatOption, Long month) {
	    // 5 (매년) - 월  
	    if (repeatOption == 5 && month != null) {
	        return month;  
	    }
	    return null;  
	}
 
	// 관리자 - 월간 일정 출력
	public List<Schedule> getAllSchedules() { 
	    return scheduleRepository.findByScheduleTypeAndScheduleStatus(3L, 0L);
	}
	
	// 관리자 - 반복 일정  
	public List<ScheduleRepeat> getAllRepeatSchedules() { 
        return scheduleRepeatRepository.findAllScheduleRepeats();
    }


}
