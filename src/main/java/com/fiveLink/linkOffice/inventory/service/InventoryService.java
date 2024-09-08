package com.fiveLink.linkOffice.inventory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.inventory.domain.Inventory;
import com.fiveLink.linkOffice.inventory.domain.InventoryCategoryDto;
import com.fiveLink.linkOffice.inventory.domain.InventoryDto;
import com.fiveLink.linkOffice.inventory.repository.InventoryCategoryRepository;
import com.fiveLink.linkOffice.inventory.repository.InventoryRepository;

import jakarta.transaction.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryCategoryRepository inventoryCategoryRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, InventoryCategoryRepository inventoryCategoryRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryCategoryRepository = inventoryCategoryRepository;
    }

    public List<InventoryDto> selectInventoryByCategoryAndDepartment(Long inventoryCategoryNo, Long departmentNo) {
        List<Inventory> inventories = inventoryRepository.findByCategoryAndDepartment(inventoryCategoryNo, departmentNo);
        List<InventoryDto> inventoryDtos = new ArrayList<>();

        for (Inventory inventory : inventories) {
            InventoryDto dto = InventoryDto.builder()
                    .inventory_no(inventory.getInventoryNo())
                    .inventory_name(inventory.getInventoryName())
                    .inventory_category_no(inventory.getInventoryCategory().getInventoryCategoryNo())
                    .inventory_price(inventory.getInventoryPrice())
                    .inventory_quantity(inventory.getInventoryQuantity())
                    .inventory_location(inventory.getInventoryLocation())
                    .inventory_purchase_date(inventory.getInventoryPurchaseDate())
                    .build();
            inventoryDtos.add(dto);
        }

        return inventoryDtos;
    }

    // 특정 부서에 대한 카테고리 요약 정보를 조회
    public List<InventoryDto> selectInventoryByDepartment(Long departmentNo) {
        List<Object[]> results = inventoryRepository.findCategorySummaryByDepartment(departmentNo);
        List<InventoryDto> inventoryDtoList = new ArrayList<>();

        for (Object[] result : results) {
            InventoryDto dto = InventoryDto.builder()
                    .inventory_category_no((Long) result[0])
                    .inventory_category_name((String) result[1])
                    .inventory_price(((Number) result[2]).intValue())
                    .inventory_quantity(((Number) result[3]).intValue())
                    .inventory_purchase_date((String) result[4])
                    .inventory_location((String) result[5])
                    .build();
            inventoryDtoList.add(dto);
        }

        return inventoryDtoList;
    }

    // 모든 부서의 목록을 조회
    public List<InventoryDto> findAllDepartments() {
        List<Object[]> results = inventoryRepository.findAllDepartments();
        List<InventoryDto> departmentDtos = new ArrayList<>();

        for (Object[] result : results) {
            InventoryDto dto = InventoryDto.builder()
                    .department_no((Long) result[0])
                    .department_name((String) result[1])
                    .build();
            departmentDtos.add(dto);
        }

        return departmentDtos;
    }
    
    public List<String> findAllCategories() {
        return inventoryRepository.findAllCategoryNames();
    }

    // 카테고리 번호와 멤버 번호를 이용해 비품 생성 또는 수정
    @Transactional
    public boolean createOrUpdateInventory(InventoryDto dto) {
        // 카테고리명으로 카테고리 번호 조회
        Long categoryNo = findCategoryNoByName(dto.getInventory_category_name());

        // 멤버 이름으로 멤버 번호 조회
        Long memberNo = findMemberNoByName(dto.getMember_name());

        // 카테고리 및 멤버 번호를 DTO에 설정
        dto.setInventory_category_no(categoryNo);
        dto.setMember_no(memberNo);

        // 수량을 제외한 동일한 비품이 있는지 찾기
        Inventory existingInventory = inventoryRepository.findByCategoryAndNameAndLocationAndPriceAndDate(
                dto.getInventory_category_name(),
                dto.getInventory_name(),
                dto.getInventory_location(),
                dto.getInventory_price(),
                dto.getInventory_purchase_date(),
                dto.getDepartment_no());

        if (existingInventory != null) {
            existingInventory.setInventoryQuantity(dto.getInventory_quantity());
            inventoryRepository.save(existingInventory);  // 업데이트
            return true;
        } else {
            Inventory newInventory = dto.toEntity();
            inventoryRepository.save(newInventory);  // 새로 생성
            return false;
        }
    }
    
    // 카테고리명으로 카테고리 번호 조회
    public Long findCategoryNoByName(String categoryName) {
        return inventoryRepository.findCategoryNoByName(categoryName);
    }

    // 멤버 이름으로 멤버 번호 조회
    public Long findMemberNoByName(String memberName) {
        return inventoryRepository.findMemberNoByName(memberName);
    }

    // 멤버 넘버로 멤버 이름 조회
    public String findMemberNameByNumber(String memberNumber) {
        return inventoryRepository.findMemberNameByMemberNumber(memberNumber);
    }
    
    // 카테고리 등록
    public String registerCategory(InventoryCategoryDto inventoryCategoryDto) {
        // 카테고리 이름으로 검색
        if (inventoryCategoryRepository.findByInventoryCategoryName(inventoryCategoryDto.getInventory_category_name()).isPresent()) {
            return "이미 존재하는 카테고리입니다.";
        }

        // 카테고리가 없으면 새로 등록
        inventoryCategoryRepository.save(inventoryCategoryDto.toEntity());
        return "카테고리가 성공적으로 등록되었습니다.";
    }

    // 비품 수정
    @Transactional
    public void updateInventory(InventoryDto dto) {
        Optional<Inventory> optionalInventory = inventoryRepository.findById(dto.getInventory_no());

        if (optionalInventory.isPresent()) {
            Inventory inventory = optionalInventory.get();
            // 수정할 값 설정
            inventory.setInventoryName(dto.getInventory_name());
            inventory.setInventoryPrice(dto.getInventory_price());
            inventory.setInventoryQuantity(dto.getInventory_quantity());
            inventory.setInventoryLocation(dto.getInventory_location());
            inventory.setInventoryPurchaseDate(dto.getInventory_purchase_date());

            inventoryRepository.save(inventory);  // 수정된 내용을 DB에 반영
        } 
    }


    // 비품 삭제
    @Transactional
    public void deleteInventory(Long no) {
        inventoryRepository.deleteById(no);  // 해당 비품을 DB에서 삭제
    }
}
