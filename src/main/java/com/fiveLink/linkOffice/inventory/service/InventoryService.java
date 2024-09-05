package com.fiveLink.linkOffice.inventory.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.inventory.domain.Inventory;
import com.fiveLink.linkOffice.inventory.domain.InventoryDto;
import com.fiveLink.linkOffice.inventory.repository.InventoryRepository;

import jakarta.transaction.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
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
    
    public String findinventoryManager() {
        return inventoryRepository.findinventoryManager();
    }
    
    @Transactional
    public boolean createOrUpdateInventory(InventoryDto dto) {
        // 수량을 제외한 동일한 비품이 있는지 찾기
        Inventory existingInventory = inventoryRepository.findByCategoryAndNameAndLocation(
            dto.getInventory_category_name(),
            dto.getInventory_name(),
            dto.getInventory_location(),
            dto.getDepartment_name());

        if (existingInventory != null) {
            // 비품이 이미 존재할 경우 수량 업데이트
            int newQuantity = existingInventory.getInventoryQuantity() + dto.getInventory_quantity();
            existingInventory.setInventoryQuantity(newQuantity);
            inventoryRepository.save(existingInventory);  // 업데이트 후 저장
            return true;  // 업데이트되었음을 알림
        } else {
            // 비품이 존재하지 않을 경우 새로 생성
            Inventory newInventory = dto.toEntity();
            inventoryRepository.save(newInventory);  // 새로운 비품 저장
            return false;  // 새로 추가되었음을 알림
        }
    }
}
