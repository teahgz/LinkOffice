package com.fiveLink.linkOffice.vacation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VacationViewController {
    
    //휴가 생성 페이지 이동
    @GetMapping("/vacation/addVacation")
    public String addVaction() {
        return "admin/vacation/addVacation";
    }
}

