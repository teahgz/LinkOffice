package com.fiveLink.linkOffice.organization.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;

import com.fiveLink.linkOffice.organization.domain.DepartmentDto;
import com.fiveLink.linkOffice.organization.service.DepartmentService;
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;

@Controller
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/department")
    public String listDepartments(Model model, @RequestParam(value = "id", required = false) Long id) {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        model.addAttribute("topLevelDepartments", departmentService.getTopLevelDepartments());
        if (id != null) {
            Optional<DepartmentDto> departmentOpt = departmentService.getDepartmentById(id);
            if (departmentOpt.isPresent()) {
                DepartmentDto department = departmentOpt.get();
                List<MemberDto> members = memberService.getMembersByDepartmentNo(department.getDepartment_no());
                department.setMembers(members);
                model.addAttribute("department", department);
            }
        }
        return "/admin/organization/department_list";
    }

    @PostMapping("/department/add")
    @ResponseBody
    public Map<String, Object> addDepartment(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String departmentName = (String) payload.get("departmentName");
            Long departmentHigh = Long.valueOf(payload.get("departmentHigh").toString());
             
            departmentService.addDepartment(departmentName, departmentHigh);
            response.put("success", true);
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException: " + e.getMessage());
            response.put("success", false);
            response.put("error", "상위 부서를 찾을 수 없습니다." + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            response.put("success", false);
            response.put("error", "서버 오류: " + e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/department/get")
    @ResponseBody
    public Map<String, Object> getDepartment(@RequestParam("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<DepartmentDto> departmentDtoOptional = departmentService.getDepartmentById(id);
            if (departmentDtoOptional.isPresent()) {
                response.put("success", true);
                response.put("department", departmentDtoOptional.get());
            } else {
                response.put("success", false);
                response.put("error", "부서를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "서버 오류: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/department/update")
    @ResponseBody
    public Map<String, Object> updateDepartment(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long departmentId = Long.valueOf(payload.get("departmentId").toString());
            String departmentName = (String) payload.get("departmentName");
            Long departmentHigh = Long.valueOf(payload.get("departmentHigh").toString());

            departmentService.updateDepartment(departmentId, departmentName, departmentHigh);
            response.put("success", true);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            response.put("success", false);
            response.put("error", "서버 오류: " + e.getMessage());
        }
        
        return response;
    }
}
