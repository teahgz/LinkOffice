package com.fiveLink.linkOffice.vacation.domain;
import lombok.*;

import java.time.LocalDateTime;

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

    public Vacation toEntity(){
        return Vacation.builder()
                .vacationNo(vacation_no)
                .vacationYear(vacation_year)
                .vacationAnnualLeave(vacation_annual_leave)
                .vacationCreateDate(vacation_create_date)
                .memberNo(member_no)
                .build();

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
