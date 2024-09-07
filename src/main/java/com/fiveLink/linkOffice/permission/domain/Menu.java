package com.fiveLink.linkOffice.permission.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fl_menu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_no")
    private Long menuNo;

    @Column(name = "menu_name")
    private String menuName;

    @Column(name = "menu_create_date")
    private LocalDateTime menuCreateDate;

    @Column(name = "menu_update_date")
    private LocalDateTime menuUpdateDate;
}
