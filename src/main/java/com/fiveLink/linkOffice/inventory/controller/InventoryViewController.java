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
import com.fiveLink.linkOffice.member.domain.MemberDto;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.organization.domain.DepartmentDto;
import com.fiveLink.linkOffice.organization.service.DepartmentService;

@Controller
public class InventoryViewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryViewController.class);

    private final InventoryService inventoryService;
    private final DepartmentService departmentService;
    private final MemberService memberService;

    @Autowired
    public InventoryViewController(MemberService memberService, DepartmentService departmentService, InventoryService inventoryService) {
    	this.memberService = memberService;
        this.inventoryService = inventoryService;
        this.departmentService = departmentService;
    }
    
    // 비품 목록 페이지
    @GetMapping("/inventory/list/{member_no}")
    public String inventoryList(@PathVariable("member_no") Long memberNo, Model model) {
        // 멤버 정보 조회
        List<MemberDto> memberdto = memberService.getMembersByNo(memberNo); 
        model.addAttribute("memberdto", memberdto);

        // 부서 목록 조회
        List<DepartmentDto> departments = departmentService.findSubDepartment();
        model.addAttribute("departments", departments);

        return "admin/inventory/inventory_list";
    }

    // 부서에 속한 비품 목록 조회
    @GetMapping("/inventory/department/{departmentNo}")
    @ResponseBody
    public List<InventoryDto> selectInventoryByDepartment(@PathVariable("departmentNo") Long departmentNo) {
        return inventoryService.selectInventoryByDepartment(departmentNo);
    }

    // 카테고리와 부서 기준 비품 조회
    @GetMapping("/inventory/category/{inventory_category_no}/department/{department_no}")
    @ResponseBody
    public List<InventoryDto> selectInventoryByCategoryAndDepartment(
            @PathVariable("inventory_category_no") Long inventoryCategoryNo,
            @PathVariable("department_no") Long departmentNo) {
        return inventoryService.selectInventoryByCategoryAndDepartment(inventoryCategoryNo, departmentNo);
    }

    // 비품 등록 페이지 
    @GetMapping("/inventory/create/{member_no}")
    public String inventoryCreate(@PathVariable("member_no") Long memberNo, Model model) {
        // 멤버 정보 조회
        List<MemberDto> memberdto = memberService.getMembersByNo(memberNo);
        model.addAttribute("memberdto", memberdto);

        // 부서 목록 조회
        List<DepartmentDto> departments = departmentService.findSubDepartment();
        model.addAttribute("departments", departments);

        // 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberNumber = authentication.getName();
        String memberName = inventoryService.findMemberNameByNumber(memberNumber);
        model.addAttribute("manager", memberName);  

        return "admin/inventory/inventory_create";
    }

    // 모든 카테고리 목록 조회
    @GetMapping("/inventory/categories")
    @ResponseBody
    public List<String> getAllCategories() {
        return inventoryService.findAllCategories();
    }

    // 비품 등록/수정 처리
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

    // 카테고리 등록 처리
    @PostMapping("/inventory/register-category")
    @ResponseBody
    public Map<String, String> registerCategory(@RequestBody InventoryCategoryDto inventoryCategoryDto) {
        Map<String, String> responseMap = new HashMap<>();

        // 입력된 카테고리명 처리
        String originalCategoryName = inventoryCategoryDto.getInventory_category_name();
        String normalizedCategoryName = originalCategoryName.replaceAll("\\s+", "");

        // 카테고리 이름이 비어 있거나 중복된 경우 처리
        if (normalizedCategoryName.isEmpty()) {
            responseMap.put("res_code", "400");
            responseMap.put("res_msg", "카테고리 이름을 입력해주세요.");
        } else if (inventoryService.isCategoryNameDuplicate(normalizedCategoryName)) {
            responseMap.put("res_code", "400");
            responseMap.put("res_msg", "카테고리 이름이 이미 존재합니다.");
        } else {
            inventoryCategoryDto.setInventory_category_name(normalizedCategoryName);
            inventoryService.registerCategory(inventoryCategoryDto);
            responseMap.put("res_code", "200");
            responseMap.put("res_msg", "카테고리가 성공적으로 등록되었습니다.");
        }

        return responseMap;
    }

    // 비품 수정 처리
    @PostMapping("/inventory/update")
    @ResponseBody
    public Map<String, String> updateInventory(@RequestBody InventoryDto dto) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            if (dto.getInventory_no() == null) {
                throw new RuntimeException("수정할 비품 번호가 없습니다.");
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

    // 비품 삭제 처리
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
