package com.fiveLink.linkOffice.organization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiveLink.linkOffice.organization.domain.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByDepartmentHighOrderByDepartmentNameAsc(Long departmentHigh);
}
