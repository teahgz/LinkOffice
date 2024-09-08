package com.fiveLink.linkOffice.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.fiveLink.linkOffice.inventory.domain.InventoryCategory;

public interface InventoryCategoryRepository extends JpaRepository<InventoryCategory, Long> {
	
	 Optional<InventoryCategory> findByInventoryCategoryName(String categoryName);
	
    
}
