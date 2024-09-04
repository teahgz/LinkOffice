package com.fiveLink.linkOffice.vacation.domain;

import lombok.*;

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
public class VacationTypeDto {
    private Long vacation_type_no;
    private String vacation_type_name;
    private int vacation_type_calculate;
    @Builder.Default
    private Map<String, String> vacationTypesData = new HashMap<>();

    // getter 및 setter 메서드
    public Map<String, String> getVacationTypesData() {
        return vacationTypesData;
    }

    public void setVacationTypesData(Map<String, String> vacationTypesData) {
        this.vacationTypesData = vacationTypesData;
    }

    public VacationType toEntity(){
        return VacationType.builder()
                .vacationTypeNo(vacation_type_no)
                .vacationTypeName(vacation_type_name)
                .vacationTypeCalculate(vacation_type_calculate)
                .build();

    }
    // 추가 메서드: vacationData를 기반으로 여러 Vacation 엔티티 생성
    public List<VacationType> toEntities() {
        List<VacationType> type = new ArrayList<>();
        for (Map.Entry<String, String> entry : vacationTypesData.entrySet()) {
            type.add(VacationType.builder()
                    .vacationTypeNo(vacation_type_no)
                    .vacationTypeName(entry.getKey())
                    .vacationTypeCalculate(Integer.parseInt(entry.getKey()))

                    .build());
        }
        return type;
    }

    public VacationTypeDto toDto(VacationType vacationType){
        return VacationTypeDto.builder()
                .vacation_type_no(vacationType.getVacationTypeNo())
                .vacation_type_name(vacationType.getVacationTypeName())
                .vacation_type_calculate(vacationType.getVacationTypeCalculate())

                .build();

    }

}
