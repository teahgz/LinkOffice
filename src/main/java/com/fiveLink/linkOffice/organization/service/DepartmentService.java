package com.fiveLink.linkOffice.organization.service;

import com.fiveLink.linkOffice.organization.domain.Department;
import com.fiveLink.linkOffice.organization.domain.DepartmentDto;
import com.fiveLink.linkOffice.organization.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public List<DepartmentDto> getAllDepartments() {
        List<Department> departments = departmentRepository.findAllByOrderByDepartmentHighAscDepartmentNameAsc();
        return buildHierarchy(mapToDto(departments));
    }

    public Optional<DepartmentDto> getDepartmentById(Long id) {
        return departmentRepository.findById(id).map(department -> {
            DepartmentDto dto = mapToDto(department);
            dto.setSubDepartments(getSubDepartments(department.getDepartmentNo()));
            return dto;
        });
    }

    public void addDepartment(String departmentName, Long departmentHigh) {
        Department department = Department.builder()
            .departmentName(departmentName)
            .departmentHigh(departmentHigh)
            .departmentStatus((long) 0) 
            .build();
        departmentRepository.save(department);
    } 
    
    public List<DepartmentDto> getTopLevelDepartments() {
        List<Department> departments = departmentRepository.findByDepartmentHigh(0L);
        return mapToDto(departments);
    }
    
    
    private DepartmentDto mapToDto(Department department) {
        return DepartmentDto.builder()
            .department_no(department.getDepartmentNo())
            .department_name(department.getDepartmentName())
            .department_high(department.getDepartmentHigh())
            .department_create_date(department.getDepartmentCreateDate())
            .department_update_date(department.getDepartmentUpdateDate())
            .department_status(department.getDepartmentStatus())
            .department_high_name(getHighDepartmentName(department.getDepartmentHigh()))
            .build();
    }

    private List<DepartmentDto> mapToDto(List<Department> departments) {
        List<DepartmentDto> dtos = new ArrayList<>();
        for (Department department : departments) {
            dtos.add(mapToDto(department));
        }
        return dtos;
    }

    private List<DepartmentDto> buildHierarchy(List<DepartmentDto> allDepartments) {
        Map<Long, DepartmentDto> departmentMap = allDepartments.stream()
            .collect(Collectors.toMap(DepartmentDto::getDepartment_no, Function.identity()));

        List<DepartmentDto> topLevelDepartments = new ArrayList<>();
        
        for (DepartmentDto department : allDepartments) {
            Long parentId = department.getDepartment_high();
            if (parentId == null || parentId == 0) {
                topLevelDepartments.add(department);
            } else {
                DepartmentDto parent = departmentMap.get(parentId);
                if (parent != null) {
                    if (parent.getSubDepartments() == null) {
                        parent.setSubDepartments(new ArrayList<>());
                    }
                    parent.getSubDepartments().add(department);
                }
            }
        }
        
        return topLevelDepartments;
    }

    private List<DepartmentDto> getSubDepartments(Long parentId) {
        List<Department> subDepartments = departmentRepository.findByDepartmentHigh(parentId);
        return mapToDto(subDepartments);
    }

    private String getHighDepartmentName(Long highDepartmentId) {
        if (highDepartmentId == null || highDepartmentId == 0) {
            return "미지정";
        }
        return departmentRepository.findById(highDepartmentId)
            .map(Department::getDepartmentName)
            .orElse("미지정");
    }
}
