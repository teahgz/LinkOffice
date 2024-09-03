package com.fiveLink.linkOffice.vacation.domain;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class VacationDto  {
    private Long vacation_no;
    private int vacation_year;
    private int vacation_annual_leave;
    private LocalDateTime vacation_create_date;
    private Long member_no;

    @Builder.Default
    private Map<String, Integer> vacationData = new HashMap<>();

    // getter 및 setter 메서드
    public Map<String, Integer> getVacationData() {
        return vacationData;
    }

    public void setVacationData(Map<String, Integer> vacationData) {
        this.vacationData = vacationData;
    }
    public Vacation toEntity(){
        return Vacation.builder()
                .vacationNo(vacation_no)
                .vacationYear(vacation_year)
                .vacationAnnualLeave(vacation_annual_leave)
                .vacationCreateDate(vacation_create_date)
                .memberNo(member_no)
                .build();

    }
    // 추가 메서드: vacationData를 기반으로 여러 Vacation 엔티티 생성
    public List<Vacation> toEntities() {
        List<Vacation> vacations = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : vacationData.entrySet()) {
            vacations.add(Vacation.builder()
                    .vacationNo(vacation_no)
                    .vacationYear(Integer.parseInt(entry.getKey()))
                    .vacationAnnualLeave(entry.getValue())
                    .vacationCreateDate(vacation_create_date)
                    .memberNo(member_no)
                    .build());
        }
        return vacations;
    }

    public VacationDto toDto(Vacation vacation){
        return VacationDto.builder()
                .vacation_no(vacation.getVacationNo())
                .vacation_year(vacation.getVacationYear())
                .vacation_annual_leave(vacation.getVacationAnnualLeave())
                .vacation_create_date(vacation.getVacationCreateDate())
                .member_no(vacation.getMemberNo())
                .build();

    }
}
