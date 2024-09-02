package com.fiveLink.linkOffice.vacation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VacationViewController {
    @GetMapping("/vacation/addVacation")
    public String addVaction() {
        return "admin/addVacation";
    }
}

