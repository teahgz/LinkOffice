package com.fiveLink.linkOffice.attendance.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fiveLink.linkOffice.attendance.domain.Attendance;
import com.fiveLink.linkOffice.attendance.repository.AttendanceRepository;
import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.repository.MemberRepository;

@Component
public class AttendanceSchedulerService {

	private final AttendanceService attendanceService;
	private final AttendanceRepository attendanceRepository;
	private final MemberRepository memberRepository;
	
	@Autowired
	public AttendanceSchedulerService(AttendanceService attendanceService,
			AttendanceRepository attendanceRepository,
			MemberRepository memberRepository) {
		this.attendanceService = attendanceService;
		this.attendanceRepository = attendanceRepository;
		this.memberRepository = memberRepository;
	}
	
	private static final Logger LOGGER
	   = LoggerFactory.getLogger(AttendanceSchedulerService.class);
	
	// 매일 오전 9시 실행됨. 스케줄러는 서버가 실행 중일 때만 동작하니까 
	@Scheduled(cron = "0 0 9 * * *") 
    public void checkAndInsertAttendance() {
		// 모든 사원 조회 
		List<Member> members = memberRepository.findAll();
        
        LocalDate today = LocalDate.now();
        
        // 어제 날짜 
        LocalDate yesterday = today.minusDays(1);

        // 어제가 주말이었다면 실행되지 않음 
        if (yesterday.getDayOfWeek() == DayOfWeek.SATURDAY || yesterday.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return; 
        }
        // 년과 달을 String으로 변환 
        String year = Integer.toString(today.getYear());
        String month = Integer.toString(today.getMonthValue());
        
        for(Member member : members) {        
        	// 공휴일이면 실행하지 않음
        	if (attendanceService.isHoliday(yesterday, year, month)) {
        		return; 
        	} else {
        		// 어제 출근했는지 조회 
        		Attendance attendance = attendanceRepository.findByMemberNoAndWorkDate(member.getMemberNo(), yesterday);
        		
    			// 어제 출근 안 했다면 날짜만 insert
    			if (attendance == null) {
    				Attendance newAttendance = Attendance.builder()
    						.memberNo(member.getMemberNo())
    						.workDate(yesterday)
    						.checkInTime(null) 
    						.checkOutTime(null) 
    						.build();
    				
    				attendanceRepository.save(newAttendance);       			
    			}
        	}
        }
    }
}
