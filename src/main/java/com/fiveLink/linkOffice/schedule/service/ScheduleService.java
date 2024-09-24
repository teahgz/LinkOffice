package com.fiveLink.linkOffice.schedule.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.schedule.domain.Schedule;
import com.fiveLink.linkOffice.schedule.domain.ScheduleDto;
import com.fiveLink.linkOffice.schedule.domain.ScheduleException;
import com.fiveLink.linkOffice.schedule.domain.ScheduleExceptionDto;
import com.fiveLink.linkOffice.schedule.domain.ScheduleRepeat;
import com.fiveLink.linkOffice.schedule.domain.ScheduleRepeatDto;
import com.fiveLink.linkOffice.schedule.repository.ScheduleExceptionRepository;
import com.fiveLink.linkOffice.schedule.repository.ScheduleRepeatRepository;
import com.fiveLink.linkOffice.schedule.repository.ScheduleRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ScheduleService { 
    private final ScheduleRepository scheduleRepository; 
    private final ScheduleRepeatRepository scheduleRepeatRepository;
    private final ScheduleExceptionRepository scheduleExceptionRepository;
    
	@Autowired
	public ScheduleService(ScheduleRepository scheduleRepository, ScheduleRepeatRepository scheduleRepeatRepository, ScheduleExceptionRepository scheduleExceptionRepositor, ScheduleExceptionRepository scheduleExceptionRepository) { 
	    this.scheduleRepository = scheduleRepository; 
	    this.scheduleRepeatRepository = scheduleRepeatRepository; 
	    this.scheduleExceptionRepository = scheduleExceptionRepository; 
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

	// 관리자 - 수정 
	public ScheduleDto getScheduleById(Long eventNo) {
        Optional<Schedule> scheduleOpt = scheduleRepository.findById(eventNo);
        if (scheduleOpt.isPresent()) {
            return convertToDto(scheduleOpt.get());
        }
        return null;
    }
 
	public ScheduleRepeatDto getScheduleRepeatById(Long eventNo) { 
	    List<ScheduleRepeat> scheduleRepeatList = scheduleRepeatRepository.findByScheduleNo(eventNo);
	    if (!scheduleRepeatList.isEmpty()) {  
	        return convertToRepeatDto(scheduleRepeatList.get(0)); 
	    }
	    return null;  
	}

    private ScheduleRepeatDto convertToRepeatDto(ScheduleRepeat scheduleRepeat) {
        ScheduleRepeatDto dto = new ScheduleRepeatDto(); 
        dto.setSchedule_repeat_type(scheduleRepeat.getScheduleRepeatType());
        dto.setSchedule_no(scheduleRepeat.getScheduleNo()); 
        dto.setSchedule_repeat_type(scheduleRepeat.getScheduleRepeatType()); 
        dto.setSchedule_repeat_day(scheduleRepeat.getScheduleRepeatDay()); 
        dto.setSchedule_repeat_week(scheduleRepeat.getScheduleRepeatWeek()); 
        dto.setSchedule_repeat_date(scheduleRepeat.getScheduleRepeatDate()); 
        dto.setSchedule_repeat_month(scheduleRepeat.getScheduleRepeatMonth()); 
        dto.setSchedule_repeat_end_date(scheduleRepeat.getScheduleRepeatEndDate()); 
        return dto;
    }

    private ScheduleDto convertToDto(Schedule schedule) { 
        ScheduleDto dto = new ScheduleDto(); 
        dto.setSchedule_no(schedule.getScheduleNo());
        dto.setMember_no(schedule.getMemberNo());
        dto.setSchedule_title(schedule.getScheduleTitle());
        dto.setSchedule_comment(schedule.getScheduleComment());
        dto.setSchedule_start_date(schedule.getScheduleStartDate());
        dto.setSchedule_end_date(schedule.getScheduleEndDate());
        dto.setSchedule_start_time(schedule.getScheduleStartTime());
        dto.setSchedule_end_time(schedule.getScheduleEndTime());
        dto.setSchedule_allday(schedule.getScheduleAllday());
        dto.setSchedule_category_no(schedule.getScheduleCategoryNo());
        dto.setSchedule_type(schedule.getScheduleType());
        dto.setSchedule_repeat(schedule.getScheduleRepeat());
        dto.setSchedule_create_date(schedule.getScheduleCreateDate());
        return dto;
    }
    
    // 관리자 - 일반 일정 수정
    public void updateCompanySchedule(Long eventId, ScheduleDto scheduleDto, ScheduleRepeatDto scheduleRepeatDto) {
        Schedule existingSchedule = scheduleRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));
         
        existingSchedule.setScheduleTitle(scheduleDto.getSchedule_title());
        existingSchedule.setScheduleComment(scheduleDto.getSchedule_comment());
        existingSchedule.setScheduleStartDate(scheduleDto.getSchedule_start_date());
        existingSchedule.setScheduleEndDate(scheduleDto.getSchedule_end_date());
        existingSchedule.setScheduleAllday(scheduleDto.getSchedule_allday());
        existingSchedule.setScheduleStartTime(scheduleDto.getSchedule_start_time());
        existingSchedule.setScheduleEndTime(scheduleDto.getSchedule_end_time());
        existingSchedule.setScheduleRepeat(scheduleDto.getSchedule_repeat());
         
        scheduleRepository.save(existingSchedule);
 
        if (scheduleDto.getSchedule_repeat() != 0) { 
            ScheduleRepeat existingRepeat = scheduleRepeatRepository.getByScheduleNo(eventId);
            
            if (existingRepeat != null) { 
                existingRepeat.setScheduleRepeatType(scheduleRepeatDto.getSchedule_repeat_type());
                existingRepeat.setScheduleRepeatDay(determineRepeatDay(scheduleRepeatDto.getSchedule_repeat_type(), scheduleRepeatDto.getSchedule_repeat_day())); // 요일
                existingRepeat.setScheduleRepeatWeek(determineRepeatWeek(scheduleRepeatDto.getSchedule_repeat_type(), scheduleRepeatDto.getSchedule_repeat_week())); // 주차
                existingRepeat.setScheduleRepeatDate(determineRepeatDate(scheduleRepeatDto.getSchedule_repeat_type(), scheduleRepeatDto.getSchedule_repeat_date())); // 일자
                existingRepeat.setScheduleRepeatMonth(determineRepeatMonth(scheduleRepeatDto.getSchedule_repeat_type(), scheduleRepeatDto.getSchedule_repeat_month())); // 월
                existingRepeat.setScheduleRepeatEndDate(scheduleRepeatDto.getSchedule_repeat_end_date()); 
 
                scheduleRepeatRepository.save(existingRepeat);
            } else { 
                ScheduleRepeat repeat = ScheduleRepeat.builder()
                        .scheduleNo(eventId)
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
    }
    
    
    // 관리자 - 반복 일정 수정
    // 이 일정만 수정
    public void updateSingleEvent(Long eventId, ScheduleDto scheduleDto, ScheduleRepeatDto scheduleRepeatDto, String pickStartDate, String pickEndDate) {
        Schedule schedule = scheduleRepository.findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다."));

        // 예외 일정으로 저장 
        ScheduleException scheduleException = ScheduleException.builder()
                .scheduleNo(schedule.getScheduleNo())
                .scheduleExceptionDate(pickStartDate)
                .scheduleExceptionTitle(scheduleDto.getSchedule_title())
                .scheduleExceptionComment(scheduleDto.getSchedule_comment())
                .scheduleExceptionStartDate(scheduleDto.getSchedule_start_date())
                .scheduleExceptionEndDate(scheduleDto.getSchedule_end_date())
                .scheduleExceptionStartTime(scheduleDto.getSchedule_start_time())
                .scheduleExceptionEndTime(scheduleDto.getSchedule_end_time())
                .scheduleExceptionCategoryNo(scheduleDto.getSchedule_category_no()) 
                .scheduleExceptionAllday(scheduleDto.getSchedule_allday())
                .scheduleExceptionStatus(0L)
                .build();
        scheduleExceptionRepository.save(scheduleException);  
    }

    // 이 일정 및 향후 일정 수정
    public void updateFutureEvents(Long eventId, ScheduleDto scheduleDto, ScheduleRepeatDto scheduleRepeatDto, String pickStartDate) {
        Schedule schedule = scheduleRepository.findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다."));
        
        ScheduleRepeat scheduleRepeat = scheduleRepeatRepository.getByScheduleNo(eventId);
        
        LocalDate adjustedEndDate = LocalDate.parse(pickStartDate).minusDays(1);
        String adjustedEndDateString = adjustedEndDate.format(DateTimeFormatter.ISO_LOCAL_DATE); 
        scheduleRepeat.setScheduleRepeatEndDate(adjustedEndDateString); 
        scheduleRepeatRepository.save(scheduleRepeat);
        
        saveCompanySchedule(scheduleDto, scheduleRepeatDto);  
    }

    // 모든 일정 수정 
    public void updateAllEvents(Long eventId, ScheduleDto scheduleDto, ScheduleRepeatDto scheduleRepeatDto) {  
        Schedule schedule = scheduleRepository.findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다."));
 
        schedule.setScheduleTitle(scheduleDto.getSchedule_title());
        schedule.setScheduleComment(scheduleDto.getSchedule_comment()); 
        schedule.setScheduleAllday(scheduleDto.getSchedule_allday());
        schedule.setScheduleCategoryNo(scheduleDto.getSchedule_category_no());
        schedule.setScheduleStartTime(scheduleDto.getSchedule_start_time());
        schedule.setScheduleEndTime(scheduleDto.getSchedule_end_time());
        schedule.setScheduleRepeat(scheduleDto.getSchedule_repeat());
        scheduleRepository.save(schedule); 
 
        if (scheduleDto.getSchedule_repeat() != 0) { 
            ScheduleRepeat existingRepeat = scheduleRepeatRepository.getByScheduleNo(eventId);
            
            if (existingRepeat != null) { 
                existingRepeat.setScheduleRepeatType(scheduleRepeatDto.getSchedule_repeat_type());
                existingRepeat.setScheduleRepeatDay(determineRepeatDay(scheduleRepeatDto.getSchedule_repeat_type(), scheduleRepeatDto.getSchedule_repeat_day())); // 요일
                existingRepeat.setScheduleRepeatWeek(determineRepeatWeek(scheduleRepeatDto.getSchedule_repeat_type(), scheduleRepeatDto.getSchedule_repeat_week())); // 주차
                existingRepeat.setScheduleRepeatDate(determineRepeatDate(scheduleRepeatDto.getSchedule_repeat_type(), scheduleRepeatDto.getSchedule_repeat_date())); // 일자
                existingRepeat.setScheduleRepeatMonth(determineRepeatMonth(scheduleRepeatDto.getSchedule_repeat_type(), scheduleRepeatDto.getSchedule_repeat_month())); // 월
                existingRepeat.setScheduleRepeatEndDate(scheduleRepeatDto.getSchedule_repeat_end_date()); 
 
                scheduleRepeatRepository.save(existingRepeat);
            } else { 
                ScheduleRepeat repeat = ScheduleRepeat.builder()
                        .scheduleNo(eventId)
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
    }  
    
    // 예외 일정 가져오기
    public List<ScheduleException> getAllExceptionSchedules() { 
	    return scheduleExceptionRepository.findByScheduleExceptionStatus(0L);
	}
    
    // 예외 일정 상세
    public ScheduleExceptionDto getScheduleExceptionById(Long eventNo) {
        Optional<ScheduleException> scheduleExceptionOpt = scheduleExceptionRepository.findById(eventNo);
        if (scheduleExceptionOpt.isPresent()) {
            return convertToExceptionDto(scheduleExceptionOpt.get());
        }
        return null;
    }
    
    private ScheduleExceptionDto convertToExceptionDto(ScheduleException scheduleException) { 
        ScheduleExceptionDto dto = new ScheduleExceptionDto(); 
        dto.setSchedule_no(scheduleException.getScheduleNo()); 
        dto.setSchedule_exception_title(scheduleException.getScheduleExceptionTitle());
        dto.setSchedule_exception_comment(scheduleException.getScheduleExceptionComment());
        dto.setSchedule_exception_start_date(scheduleException.getScheduleExceptionStartDate());
        dto.setSchedule_exception_end_date(scheduleException.getScheduleExceptionEndDate());
        dto.setSchedule_exception_start_time(scheduleException.getScheduleExceptionStartTime());
        dto.setSchedule_exception_end_time(scheduleException.getScheduleExceptionEndTime());
        dto.setSchedule_exception_allday(scheduleException.getScheduleExceptionAllday());
        dto.setSchedule_exception_category_no(scheduleException.getScheduleExceptionCategoryNo());  
        dto.setSchedule_exception_create_date(scheduleException.getScheduleExceptionCreateDate());
        return dto;
    }
    
    // 예외 일정 수정
    public void updateCompanyExceptionSchedule(Long eventId, ScheduleExceptionDto scheduleExceptionDto) {
        ScheduleException existingSchedule = scheduleExceptionRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));
         
        existingSchedule.setScheduleExceptionTitle(scheduleExceptionDto.getSchedule_exception_title());
        existingSchedule.setScheduleExceptionComment(scheduleExceptionDto.getSchedule_exception_comment());
        existingSchedule.setScheduleExceptionStartDate(scheduleExceptionDto.getSchedule_exception_start_date());
        existingSchedule.setScheduleExceptionEndDate(scheduleExceptionDto.getSchedule_exception_end_date());
        existingSchedule.setScheduleExceptionAllday(scheduleExceptionDto.getSchedule_exception_allday());
        existingSchedule.setScheduleExceptionStartTime(scheduleExceptionDto.getSchedule_exception_start_time());
        existingSchedule.setScheduleExceptionEndTime(scheduleExceptionDto.getSchedule_exception_end_time()); 
         
        scheduleExceptionRepository.save(existingSchedule); 
    }
}
