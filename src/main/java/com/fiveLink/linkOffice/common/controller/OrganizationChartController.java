package com.fiveLink.linkOffice.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrganizationChartController {

    @GetMapping("/organization_chart")
    public String showOrganizationChart() {
        return "organization_chart";   
    }
}
