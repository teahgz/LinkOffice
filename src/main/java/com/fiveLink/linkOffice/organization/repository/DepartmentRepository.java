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

    List<Department> findAllByOrderByDepartmentHighAscDepartmentNameAsc();
    
    @Query(value = "SELECT * FROM departments WHERE department_name = :name AND department_id = :id", nativeQuery = true)
    Department findByNameAndId(@Param("name") String name, @Param("id") Long id);
 
    // 부서명 수정 중복 확인
    boolean existsByDepartmentName(String departmentName);
    boolean existsByDepartmentNameAndDepartmentNoNot(String departmentName, Long departmentNo); 
}
