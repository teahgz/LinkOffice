package com.fiveLink.linkOffice.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fiveLink.linkOffice.inventory.domain.Inventory;




public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT ic.inventoryCategoryNo, " + 
            "ic.inventoryCategoryName, " +
            "AVG(i.inventoryPrice) AS average_price, " +
            "SUM(i.inventoryQuantity) AS total_quantity, " +
            "MAX(i.inventoryPurchaseDate) AS latest_purchase_date, " +
            "MAX(i.inventoryLocation) AS latest_location " + 
           "FROM Inventory i " +
           "JOIN i.inventoryCategory ic " +
           "GROUP BY ic.inventoryCategoryName " +
           "ORDER BY ic.inventoryCategoryNo")
    List<Object[]> findCategorySummary();
    
    List<Inventory> findByInventoryCategoryInventoryCategoryNo(Long inventoryCategoryNo);
    
    List<Inventory> findByDepartmentDepartmentNo(Long departmentNo);
}
