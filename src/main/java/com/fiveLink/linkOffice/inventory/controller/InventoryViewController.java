package com.fiveLink.linkOffice.inventory.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fiveLink.linkOffice.inventory.domain.InventoryDto;
import com.fiveLink.linkOffice.inventory.service.InventoryService;

@Controller
public class InventoryViewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryViewController.class);

    private final InventoryService inventoryService;

    @Autowired
    public InventoryViewController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // 메인 화면 - 부서 목록 및 카테고리 요약 목록 조회
    @GetMapping("/inventory/list")
    public String selectInventoryList(Model model) {
        // 부서 목록을 조회하여 model에 추가
        List<InventoryDto> departmentNames = inventoryService.findAllDepartments();
        model.addAttribute("departments", departmentNames);

        return "admin/inventory/inventory_list";
    }

    @GetMapping("/inventory/category/{inventory_category_no}/department/{department_no}")
    @ResponseBody
    public List<InventoryDto> selectInventoryByCategoryAndDepartment(
            @PathVariable("inventory_category_no") Long inventoryCategoryNo,
            @PathVariable("department_no") Long departmentNo) {
        return inventoryService.selectInventoryByCategoryAndDepartment(inventoryCategoryNo, departmentNo);
    }

    @GetMapping("/inventory/department/{departmentNo}")
    @ResponseBody
    public List<InventoryDto> selectInventoryByDepartment(@PathVariable("departmentNo") Long departmentNo) {
        List<InventoryDto> result = inventoryService.selectInventoryByDepartment(departmentNo);
        LOGGER.info("selectInventoryByDepartment result: {}", result);
        return result;
    }
    
    @GetMapping("/inventory/create")
    public String selectInventorycreate(Model model) {
        // 부서 목록을 조회하여 model에 추가
        List<InventoryDto> departmentNames = inventoryService.findAllDepartments();
        model.addAttribute("departments", departmentNames);

        return "admin/inventory/inventory_create";
    }
}
