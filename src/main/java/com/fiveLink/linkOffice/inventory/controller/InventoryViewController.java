package com.fiveLink.linkOffice.inventory.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
        String finventoryManager = inventoryService.findinventoryManager();
        model.addAttribute("departments", departmentNames);
        model.addAttribute("manager", finventoryManager);
        return "admin/inventory/inventory_create";
    }
    
    @GetMapping("/inventory/categories")
    @ResponseBody
    public List<String> getAllCategories() {
        return inventoryService.findAllCategories();
    }
    
    @ResponseBody
    @PostMapping("/submit-inventory")
    public Map<String, String> createOrUpdateInventory(
        @RequestParam("department") Long departmentNo,
        @RequestParam("date") String date,
        @RequestParam("category") String categoryName,
        @RequestParam("inventoryName") String inventoryName,
        @RequestParam("inventoryLocation") String inventoryLocation,
        @RequestParam("inventoryPrice") Integer inventoryPrice, 
        @RequestParam("inventoryManager") String memberName,
        @RequestParam("inventoryQuantity") Integer inventoryQuantity 
    ) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "404");
        resultMap.put("res_msg", "비품 처리 중 오류가 발생했습니다.");

        
        // DTO 생성 및 값 설정
        InventoryDto dto = InventoryDto.builder()
            .department_no(departmentNo)
            .inventory_purchase_date(date)
            .inventory_category_name(categoryName)
            .inventory_name(inventoryName)
            .inventory_location(inventoryLocation)
            .inventory_price(inventoryPrice)
            .member_name(memberName)
            .inventory_quantity(inventoryQuantity)
            .build();

        try {
            // 서비스 호출해서 업데이트 또는 생성
            boolean isUpdated = inventoryService.createOrUpdateInventory(dto);

            if (isUpdated) {
                resultMap.put("res_code", "200");
                resultMap.put("res_msg", "비품이 성공적으로 업데이트되었습니다.");
            } else {
                resultMap.put("res_code", "200");
                resultMap.put("res_msg", "새로운 비품이 성공적으로 등록되었습니다.");
            }
        } catch (Exception e) {
            LOGGER.error("Exception during inventory processing: ", e);
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "비품 처리 중 오류가 발생했습니다.");
        }

        return resultMap;
    }
    
}
