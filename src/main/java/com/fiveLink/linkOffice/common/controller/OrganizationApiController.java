//package com.fiveLink.linkOffice.common.controller;
//
//import com.fiveLink.linkOffice.common.service.OrganizationService;
//import com.fiveLink.linkOffice.member.domain.Member;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/organization")
//public class OrganizationApiController {
//
//    private final OrganizationService organizationService;
//
//    @Autowired
//    public OrganizationApiController(OrganizationService organizationService) {
//        this.organizationService = organizationService;
//    }
//
//    @GetMapping("/members/department/{departmentNo}")
//    public List<Member> getMembersByDepartment(@PathVariable Long departmentNo) {
//        return organizationService.getMembersByDepartment(departmentNo);
//    }
//
//    @GetMapping("/members/position/{positionNo}")
//    public List<Member> getMembersByPosition(@PathVariable Long positionNo) {
//        return organizationService.getMembersByPosition(positionNo);
//    }
//}
