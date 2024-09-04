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
import com.fiveLink.linkOffice.organization.domain.Department;

@Controller
public class InventoryViewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryViewController.class);

    private final InventoryService inventoryService;

    @Autowired
    public InventoryViewController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/inventory/list")
    public String selectInventoryList(Model model) {
        List<InventoryDto> categorySummaryList = inventoryService.selectCategorySummary();
        List<InventoryDto> departmentNames = inventoryService.findAllDepartments();
        
        LOGGER.info("Category Summary List: " + categorySummaryList);
        LOGGER.info("Department Names: " + departmentNames);
        model.addAttribute("categorySummaryList", categorySummaryList);
        model.addAttribute("departments", departmentNames);
        return "admin/inventory/list";
    }
    
    @GetMapping("/inventory/category/{inventory_category_no}")
    @ResponseBody
    public List<InventoryDto> selectInventoryByCategory(@PathVariable("inventory_category_no") Long inventory_category_no) {
        return inventoryService.selectInventoryByCategory(inventory_category_no);
    }
    
    
    
    
}
