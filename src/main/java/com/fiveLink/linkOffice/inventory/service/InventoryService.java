package com.fiveLink.linkOffice.inventory.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.inventory.controller.InventoryViewController;
import com.fiveLink.linkOffice.inventory.domain.Inventory;
import com.fiveLink.linkOffice.inventory.domain.InventoryDto;
import com.fiveLink.linkOffice.inventory.repository.InventoryRepository;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryViewController.class);
    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<InventoryDto> selectCategorySummary() {
        List<Object[]> results = inventoryRepository.findCategorySummary();
        List<InventoryDto> inventoryDtoList = new ArrayList<>();

        for (Object[] result : results) {
            InventoryDto dto = InventoryDto.builder()
                    .inventory_category_no((Long) result[0])
                    .inventory_category_name((String) result[1])
                    .inventory_price(((Number) result[2]).intValue())   // 평균 가격
                    .inventory_quantity(((Number) result[3]).intValue()) // 총 수량
                    .inventory_purchase_date((String) result[4])         // 최근 구매일자
                    .inventory_location((String) result[5])              // 최근 등록 위치
                    .build();
            LOGGER.info("DTO: " + dto.toString());
            inventoryDtoList.add(dto);
        }

        return inventoryDtoList;
    }
    
    public List<InventoryDto> selectInventoryByCategory(Long inventory_category_no) {
        List<Inventory> inventories = inventoryRepository.findByInventoryCategoryInventoryCategoryNo(inventory_category_no);
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
    public List<InventoryDto> findAllDepartments() {
        List<Inventory> inventories = inventoryRepository.findByDepartmentDepartmentNo(inventory_department_no);
        List<InventoryDto> departmentDtos = new ArrayList<>();

        for (Inventory inventory : inventories) {
            InventoryDto dto = InventoryDto.builder()
                    .department_no(inventory.getDepartment().getDepartmentNo())
                    .department_name(inventory.getDepartment().getDepartmentName())
                    .build();
            departmentDtos.add(dto);
        }

        return departmentDtos;
    }
}
