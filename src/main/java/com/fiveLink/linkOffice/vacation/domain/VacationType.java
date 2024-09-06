package com.fiveLink.linkOffice.vacation.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="fl_vacation_type")
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Builder
public class VacationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="vacation_type_no")
    private Long vacationTypeNo;

    @Column(name="vacation_type_name")
    private String vacationTypeName;

    @Column(name="vacation_type_calculate")
    private double vacationTypeCalculate;


}
