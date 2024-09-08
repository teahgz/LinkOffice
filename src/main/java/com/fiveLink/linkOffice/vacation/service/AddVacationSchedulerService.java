package com.fiveLink.linkOffice.vacation.service;

import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.repository.MemberRepository;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.vacation.repository.VacationCheckRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class AddVacationSchedulerService {

    private final MemberRepository memberRepository;
    private final VacationCheckRepository vacationCheckRepository;
    private final MemberService memberService;
    private final VacationService vacationService;
    @Autowired
    public AddVacationSchedulerService(MemberRepository memberRepository, VacationService vacationService, VacationCheckRepository vacationCheckRepository, MemberService memberService) {
        this.memberRepository = memberRepository;
        this.vacationCheckRepository = vacationCheckRepository;
        this.memberService = memberService;
        this.vacationService = vacationService;
    }
    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
// @Scheduled(cron = "*/5 * * * * ?")
@Transactional
    public void addVacationScheduler() {

        // 1년 미만 재직자 처리
        List<MemberDto> underOneYearMembers = memberService.selectUnderYearMember(1);
        for (MemberDto dto : underOneYearMembers) {
            if (dto.getMember_vacation_date() == null || dto.getMember_vacation_date().isEmpty()) {
                if (firstVacation(dto.getMember_hire_date())) {
                    int monthDif = (int) Period.between(LocalDate.parse(dto.getMember_hire_date()), LocalDate.now()).toTotalMonths();
                    //만약에 테스트용으로 입사기준 날짜가 다다르게 들어갈 경우를 대비해서 3개월 차이 나면 3개입력될 수 있도록 구성
                    vacationService.incrementVacation(dto.getMember_no(), monthDif);
                }
            } else {
                if (monthVacation(dto.getMember_vacation_date())) {
                    vacationService.incrementVacation(dto.getMember_no(), 1);
                }
            }
        }
        // 1년 이상 재직자 처리
        List<MemberDto> overOneYearMembers = memberService.selectUnderYearMember(0);
        for (MemberDto dto : overOneYearMembers) {
            if (oneUpJoined(dto.getMember_hire_date())) {
                // 입사일 기준으로 1년이 경과한 경우 새로운 휴가 지급
                int yearSinceJoin = Period.between(LocalDate.parse(dto.getMember_hire_date()), LocalDate.now()).getYears();
                int vacationDay = getVacationDaysByYears(yearSinceJoin);
                vacationService.resetVacation(dto.getMember_no(), vacationDay);
            }
        }
    }

    // 입사일 기준 첫 번째 휴가 지급 시점 확인
    private boolean firstVacation(String joiningDate) {
        try {
            LocalDate joinDate = LocalDate.parse(joiningDate);
            return Period.between(joinDate, LocalDate.now()).toTotalMonths() >= 1;
        } catch (DateTimeParseException e) {
            // 날짜 파싱 실패 시 예외 처리
            return false;
        }
    }


    // 마지막 휴가 지급일 기준으로 한 달 경과 확인
    private boolean monthVacation(String lastVacationDateStr) {
        try {
            if (lastVacationDateStr == null || lastVacationDateStr.isEmpty()) {
                return false;
            }
            LocalDate lastVacationDate = LocalDate.parse(lastVacationDateStr);
            return Period.between(lastVacationDate, LocalDate.now()).toTotalMonths() >= 1;
        } catch (DateTimeParseException e) {
            // 날짜 파싱 실패 시 예외 처리
            return false;
        }
    }

    // 1년 경과 확인
    private boolean oneUpJoined(String joiningDate) {
        try {
            LocalDate jDate = LocalDate.parse(joiningDate);
            return Period.between(jDate, LocalDate.now()).getYears() >= 1;
        } catch (DateTimeParseException e) {
            // 날짜 파싱 실패 시 예외 처리
            return false;
        }
    }

    // 연차에 따른 휴가 개수 반환
    private int getVacationDaysByYears(int years) {
        // 회사 정책에 따른 휴가 개수 로직 구현
        if (years >= 10) {
            return 20; // 예시: 10년 이상일 경우 20일
        } else if (years >= 5) {
            return 15; // 예시: 5년 이상일 경우 15일
        }
        return 10; // 기본 10일
    }

}
