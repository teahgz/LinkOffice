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
}
