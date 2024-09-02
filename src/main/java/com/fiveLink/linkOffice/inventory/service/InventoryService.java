package com.fiveLink.linkOffice.inventory.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.inventory.controller.InventoryViewController;
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
}
