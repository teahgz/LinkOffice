package com.fiveLink.linkOffice.inventory.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fiveLink.linkOffice.inventory.domain.InventoryCategoryDto;
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
        return result;
    }
    
    @GetMapping("/inventory/create")
    public String selectInventoryCreate(Model model) {
        // 부서 목록을 조회하여 model에 추가
        List<InventoryDto> departmentNames = inventoryService.findAllDepartments();

        // Spring Security에서 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberNumber = authentication.getName();  
        
        // member_number로 member_name을 조회
        String memberName = inventoryService.findMemberNameByNumber(memberNumber);
        
        model.addAttribute("departments", departmentNames);
        model.addAttribute("manager", memberName);  
        
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
        
        // '관리자'라는 단어 제거
        memberName = memberName.replace("관리자", "").trim();
        
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
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "비품 처리 중 오류가 발생했습니다.");
        }

        return resultMap;
    }

    
    @PostMapping("/inventory/register-category")
    @ResponseBody
    public Map<String, String> registerCategory(@RequestBody InventoryCategoryDto inventoryCategoryDto) {
        Map<String, String> responseMap = new HashMap<>();

        // 원본 카테고리 이름
        String originalCategoryName = inventoryCategoryDto.getInventory_category_name();
        // 공백을 모두 제거한 카테고리 이름
        String normalizedCategoryName = originalCategoryName.replaceAll("\\s+", "");

        // 공백을 모두 제거한 이름이 비어 있는 경우 처리
        if (normalizedCategoryName.isEmpty()) {
            responseMap.put("res_code", "400");
            responseMap.put("res_msg", "카테고리 이름을 입력해주세요.");
        }
        // 공백을 제거한 이름이 이미 존재하는 경우
        else if (inventoryService.isCategoryNameDuplicate(normalizedCategoryName)) {
            responseMap.put("res_code", "400");
            responseMap.put("res_msg", "카테고리 이름이 이미 존재합니다.");
        }
        // 중복이 아닌 경우 카테고리 등록
        else {
            // DTO에 공백이 제거된 이름을 설정하여 저장
            inventoryCategoryDto.setInventory_category_name(normalizedCategoryName);
            inventoryService.registerCategory(inventoryCategoryDto);
            responseMap.put("res_code", "200");
            responseMap.put("res_msg", "카테고리가 성공적으로 등록되었습니다.");
        }

        return responseMap;
    }



    
    @PostMapping("/inventory/update")
    @ResponseBody
    public Map<String, String> updateInventory(@RequestBody InventoryDto dto) {
        Map<String, String> resultMap = new HashMap<>();
        try {

            if (dto.getInventory_no() == null) {
                throw new RuntimeException("수정할 비품의 번호가 없습니다.");
            }

            inventoryService.updateInventory(dto);
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "비품이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "수정 중 오류가 발생했습니다. " + e.getMessage());
        }
        return resultMap;
    }




    @PostMapping("/inventory/delete/{no}")
    @ResponseBody
    public Map<String, String> deleteInventory(@PathVariable("no") Long no) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            inventoryService.deleteInventory(no);
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "비품이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            resultMap.put("res_code", "500");
            resultMap.put("res_msg", "삭제 중 오류가 발생했습니다.");
        }
        return resultMap;
    }
    
}
