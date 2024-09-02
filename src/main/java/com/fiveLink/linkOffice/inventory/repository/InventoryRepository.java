package com.fiveLink.linkOffice.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiveLink.linkOffice.inventory.domain.Inventory;


public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	Inventory findByinventoryNo(Long inventory_no);
	
}
