package com.fiveLink.linkOffice.organization.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PositionDto {

    private Long position_no;
    private String position_name;
    private Long position_high;
    private LocalDateTime position_create_date;
    private LocalDateTime position_update_date;
    private Long position_status;
 
    public static PositionDto toDto(Position position) {
        return PositionDto.builder()
                .position_no(position.getPositionNo())
                .position_name(position.getPositionName())
                .position_high(position.getPositionHigh())
                .position_create_date(position.getPositionCreateDate())
                .position_update_date(position.getPositionUpdateDate())
                .position_status(position.getPositionStatus())
                .build();
    }
 
    public Position toEntity() {
        return Position.builder()
                .positionNo(position_no)
                .positionName(position_name)
                .positionHigh(position_high)
                .positionCreateDate(position_create_date)
                .positionUpdateDate(position_update_date)
                .positionStatus(position_status)
                .build();
    }
}
