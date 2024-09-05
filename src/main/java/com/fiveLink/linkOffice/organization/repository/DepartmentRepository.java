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
    
    List<Department> findAllByOrderByDepartmentHighAscDepartmentNameAsc();
    
    // 부서명 수정 중복 확인
    boolean existsByDepartmentName(String departmentName);
    boolean existsByDepartmentNameAndDepartmentNoNot(String departmentName, Long departmentNo); 
}
