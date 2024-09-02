package com.fiveLink.linkOffice.vacation.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="fl_member")
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Builder
public class Vacation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="vacation_no")
    private Long vacationNo;
    // 디비 컬럼 확인 후 진행

}
