package com.fiveLink.linkOffice.organization.repository;

import com.fiveLink.linkOffice.organization.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByDepartmentHigh(Long departmentHigh);

    List<Department> findByDepartmentHighAndDepartmentStatus(Long departmentHigh, Long departmentStatus);
    
    List<Department> findAllByDepartmentStatusOrderByDepartmentHighAscDepartmentNameAsc(Long departmentStatus);
    
    // 부서명 수정 중복 확인
    boolean existsByDepartmentNameAndDepartmentStatus(String departmentName, Long departmentStatus);   
    
    @Query("SELECT d FROM Department d WHERE d.departmentHigh = :departmentNo AND d.departmentStatus = 0")
    List<Department> findSubDepartmentsByDepartmentNo(@Param("departmentNo") Long departmentNo);

    // [전주영] 사원 등록 (부서명조회)
    @Query(value = "SELECT d1 FROM Department d1 LEFT JOIN Department d2 ON d1.departmentNo = d2.departmentHigh " +
            "WHERE d2.departmentHigh IS NULL AND d1.departmentStatus = 0 "
            + "ORDER BY d1.departmentName")
    List<Department> findDepartmentsWithoutSubDepartments();
}
