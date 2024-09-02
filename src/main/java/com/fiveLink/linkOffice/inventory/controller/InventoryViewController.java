package com.fiveLink.linkOffice.inventory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fiveLink.linkOffice.inventory.domain.InventoryDto;
import com.fiveLink.linkOffice.inventory.service.InventoryService;

@Controller
public class InventoryViewController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryViewController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/inventory/list")
    public String selectInventoryList(Model model) {
        List<InventoryDto> inventoryList = inventoryService.selectInventoryList();
        model.addAttribute("inventoryList", inventoryList);
        System.out.println("Inventory List: " + inventoryList);
        return "inventory/list";
    }
}
