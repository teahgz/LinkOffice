package com.fiveLink.linkOffice.schedule.domain;

import java.time.LocalDateTime;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ScheduleExceptionDto {

    private Long schedule_exception_no;
    private Long schedule_no; 
    private String schedule_exception_date;
    private String schedule_exception_title;
    private String schedule_exception_comment;
    private String schedule_exception_start_date;
    private String schedule_exception_end_date;
    private String schedule_exception_start_time;
    private String schedule_exception_end_time;
    private Long schedule_exception_allday;
    private Long schedule_exception_category_no;
    private LocalDateTime schedule_exception_create_date;
    private Long schedule_exception_status;

    public ScheduleException toEntity() {
        return ScheduleException.builder()
                .scheduleExceptionNo(schedule_exception_no)
                .scheduleNo(schedule_no) 
                .scheduleExceptionDate(schedule_exception_date)
                .scheduleExceptionTitle(schedule_exception_title)
                .scheduleExceptionComment(schedule_exception_comment)
                .scheduleExceptionStartDate(schedule_exception_start_date)
                .scheduleExceptionEndDate(schedule_exception_end_date)
                .scheduleExceptionStartTime(schedule_exception_start_time)
                .scheduleExceptionEndTime(schedule_exception_end_time)
                .scheduleExceptionAllday(schedule_exception_allday)
                .scheduleExceptionCategoryNo(schedule_exception_category_no)
                .scheduleExceptionCreateDate(schedule_exception_create_date)
                .scheduleExceptionStatus(schedule_exception_status)
                .build();
    }

    public static ScheduleExceptionDto toDto(ScheduleException scheduleException) {
        return ScheduleExceptionDto.builder()
                .schedule_exception_no(scheduleException.getScheduleExceptionNo())
                .schedule_no(scheduleException.getScheduleNo()) 
                .schedule_exception_date(scheduleException.getScheduleExceptionDate())
                .schedule_exception_title(scheduleException.getScheduleExceptionTitle())
                .schedule_exception_comment(scheduleException.getScheduleExceptionComment())
                .schedule_exception_start_date(scheduleException.getScheduleExceptionStartDate())
                .schedule_exception_end_date(scheduleException.getScheduleExceptionEndDate())
                .schedule_exception_start_time(scheduleException.getScheduleExceptionStartTime())
                .schedule_exception_end_time(scheduleException.getScheduleExceptionEndTime())
                .schedule_exception_allday(scheduleException.getScheduleExceptionAllday())
                .schedule_exception_category_no(scheduleException.getScheduleExceptionCategoryNo())
                .schedule_exception_create_date(scheduleException.getScheduleExceptionCreateDate())
                .schedule_exception_status(scheduleException.getScheduleExceptionStatus())
                .build();
    }
}