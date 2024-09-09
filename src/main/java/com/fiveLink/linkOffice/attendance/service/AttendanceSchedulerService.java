package com.fiveLink.linkOffice.attendance.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceSchedulerService.class);
    
    // 매일 오전 9시 10분에 실행됨
    @Scheduled(cron = "0 10 9 * * *") 
    public void checkAndInsertAttendance() {
        // 모든 사원 조회 
        List<Member> members = memberRepository.findAll();
        
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Member member : members) {
            // 입사일 문자열을 LocalDate로 변환
            LocalDate hireDate = LocalDate.parse(member.getMemberHireDate(), dateFormatter);

            // 입사일이 어제 날짜보다 후인 경우 무시
            if (hireDate.isAfter(yesterday)) {
                continue;
            }
            
            LocalDate dateToCheck = hireDate;
            
            while (!dateToCheck.isAfter(yesterday)) {
                // 주말인지 확인
                if (dateToCheck.getDayOfWeek() == DayOfWeek.SATURDAY || dateToCheck.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    dateToCheck = dateToCheck.plusDays(1);
                    continue;
                }
                
                // 공휴일인지 확인
                String year = Integer.toString(dateToCheck.getYear());
                String month = Integer.toString(dateToCheck.getMonthValue());
                
                if (attendanceService.isHoliday(dateToCheck, year, month)) {
                    dateToCheck = dateToCheck.plusDays(1);
                    continue;
                }
                
                // DB에 attendance 데이터 존재 여부 확인
                Attendance attendance = attendanceRepository.findByMemberNoAndWorkDate(member.getMemberNo(), dateToCheck);
                
                // 출근 기록이 없으면 새로 추가
                if (attendance == null) {
                    Attendance newAttendance = Attendance.builder()
                            .memberNo(member.getMemberNo())
                            .workDate(dateToCheck)
                            .checkInTime(null)
                            .checkOutTime(null)
                            .build();
                    
                    attendanceRepository.save(newAttendance);
                    LOGGER.info("Inserted attendance record for member {} on {}", member.getMemberNo(), dateToCheck);
                }
                
                // 다음 날로 이동
                dateToCheck = dateToCheck.plusDays(1);
            }
        }
    }
}
