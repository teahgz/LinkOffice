package com.fiveLink.linkOffice.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fiveLink.linkOffice.inventory.domain.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	
    // 부서에 따른 카테고리 요약 정보 조회
    @Query("SELECT ic.inventoryCategoryNo, " + 
            "ic.inventoryCategoryName, " +
            "AVG(i.inventoryPrice) AS average_price, " +
            "SUM(i.inventoryQuantity) AS total_quantity, " +
            "MAX(i.inventoryPurchaseDate) AS latest_purchase_date, " +
            "MAX(i.inventoryLocation) AS latest_location " + 
            "FROM Inventory i " +
            "JOIN i.inventoryCategory ic " +
            "JOIN i.department d " +
            "WHERE d.departmentNo = :departmentNo " +
            "GROUP BY ic.inventoryCategoryName " +
            "ORDER BY ic.inventoryCategoryNo")
    List<Object[]> findCategorySummaryByDepartment(@Param("departmentNo") Long departmentNo);

    // 모든 부서 목록 조회
    @Query("SELECT DISTINCT d.departmentNo, d.departmentName " +
           "FROM Inventory i " +
           "JOIN i.department d")
    List<Object[]> findAllDepartments();
    
    @Query("SELECT i FROM Inventory i " +
		       "WHERE i.inventoryCategory.inventoryCategoryNo = :inventoryCategoryNo " +
		       "AND i.department.departmentNo = :departmentNo " +
    	       "ORDER BY i.inventoryPurchaseDate DESC") 
		List<Inventory> findByCategoryAndDepartment(@Param("inventoryCategoryNo") Long inventoryCategoryNo, 
		                                            @Param("departmentNo") Long departmentNo);
}
