package com.fiveLink.linkOffice.organization.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.mapper.VacationMapper;
import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.repository.MemberRepository;
import com.fiveLink.linkOffice.organization.domain.Department;
import com.fiveLink.linkOffice.organization.domain.DepartmentDto;
import com.fiveLink.linkOffice.organization.repository.DepartmentRepository;
import com.fiveLink.linkOffice.vacation.repository.VacationRepository;

import jakarta.transaction.Transactional;

@Service
public class DepartmentService {

    private DepartmentRepository departmentRepository;
    private MemberRepository memberRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository, MemberRepository memberRepository){
        this.departmentRepository = departmentRepository;
        this.memberRepository = memberRepository;
    }
    
    public List<DepartmentDto> getAllDepartments() {
        List<Department> departments = departmentRepository.findAllByDepartmentStatusOrderByDepartmentHighAscDepartmentNameAsc(0L);
        return buildHierarchy(mapToDto(departments));
    }

    // 특정 부서의 정보 반환
    public Optional<DepartmentDto> getDepartmentById(Long id) {
        return departmentRepository.findById(id).map(department -> {
            DepartmentDto dto = mapToDto(department);
            dto.setSubDepartments(getSubDepartments(department.getDepartmentNo()));
            return dto;
        });
    }

    // 부서 등록
    @Transactional
    public void addDepartment(String departmentName, Long departmentHigh) {
        try {
        	// 중복 부서명 체크
            boolean isDuplicate = departmentRepository.existsByDepartmentNameAndDepartmentStatus(departmentName, 0L);
            if (isDuplicate) {
                throw new CustomDuplicateDepartmentException("중복된 부서명이 존재합니다.");
            }
            
            Department department = Department.builder()
                .departmentName(departmentName)
                .departmentHigh(departmentHigh)
                .departmentStatus(0L)
                .build();
             
            department = departmentRepository.save(department);
     
            Long newDepartmentNo = department.getDepartmentNo();
             
            if (departmentHigh != null && departmentHigh != 0) { 
                List<Member> members = memberRepository.findByDepartmentNo(departmentHigh);
                 
                if (!members.isEmpty()) {
                    for (Member member : members) {
                        member.setDepartmentNo(newDepartmentNo); 
                    } 
                    memberRepository.saveAll(members);
                }
            }
        } catch (DataIntegrityViolationException e) {  
            throw new CustomDuplicateDepartmentException("중복된 부서명이 존재합니다.");
        }
    }
    
    public static class CustomDuplicateDepartmentException extends RuntimeException {
        public CustomDuplicateDepartmentException(String message) {
            super(message);
        }
    }
    
    public List<DepartmentDto> getTopLevelDepartments() { 
        List<Department> departments = departmentRepository.findByDepartmentHighAndDepartmentStatus(0L, 0L);
        return departments.stream()
                          .map(this::mapToDto)
                          .collect(Collectors.toList());
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

    // 부서 계층 구조 
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

    // 하위 부서 목록 
    private List<DepartmentDto> getSubDepartments(Long parentId) {
        List<Department> subDepartments = departmentRepository.findByDepartmentHigh(parentId);
        return mapToDto(subDepartments);
    }

    // 상위 부서명
    private String getHighDepartmentName(Long highDepartmentId) {
        if (highDepartmentId == null || highDepartmentId == 0) {
            return "미지정";
        }
        return departmentRepository.findById(highDepartmentId)
            .map(Department::getDepartmentName)
            .orElse("미지정");
    }

    // 부서 수정
    @Transactional
    public void updateDepartment(Long departmentId, String departmentName, Long departmentHigh) {
        Department existingDepartment = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("부서를 찾을 수 없습니다."));

        // 중복 부서명 체크
        boolean isDuplicate = departmentRepository.existsByDepartmentNameAndDepartmentStatus(departmentName, 0L);
        if (isDuplicate) {
            throw new CustomDuplicateDepartmentException("중복된 부서명이 존재합니다.");
        }

        try {
            existingDepartment.setDepartmentName(departmentName);
            existingDepartment.setDepartmentHigh(departmentHigh);
            departmentRepository.save(existingDepartment);
        } catch (DataIntegrityViolationException e) {
            throw new CustomDuplicateDepartmentException("중복된 부서명이 존재합니다.");
        }
    }

    // 부서 삭제
    public long getMemberCountByDepartmentNo(Long departmentNo) {
        return memberRepository.countByDepartmentNo(departmentNo);
    }

    @Transactional
    public boolean deleteDepartment(Long departmentId) {
        Optional<Department> departmentOpt = departmentRepository.findById(departmentId);

        if (departmentOpt.isPresent()) {
            Department department = departmentOpt.get();
            List<Department> subDepartments = departmentRepository.findByDepartmentHigh(departmentId);

            if (subDepartments.isEmpty()) {
                // 부서 소속 사원 수
                long memberCount = memberRepository.countByDepartmentNo(departmentId);

                if (memberCount > 0) {
                    return false;
                } else {
                    department.setDepartmentStatus(1L);
                    departmentRepository.save(department);
                    return true;
                }
            } else {
                // 하위 부서 소속 사원 존재 여부
                boolean hasMembersInSubDepartments = subDepartments.stream()
                    .anyMatch(sub -> memberRepository.countByDepartmentNo(sub.getDepartmentNo()) > 0);

                if (hasMembersInSubDepartments) {
                    return false;
                } else {
                    department.setDepartmentStatus(1L);
                    departmentRepository.save(department);

                    for (Department subDepartment : subDepartments) {
                        subDepartment.setDepartmentStatus(1L);
                        departmentRepository.save(subDepartment);
                    }
                    return true;
                }
            }
        }
        return false;
    }
} 