package com.fiveLink.linkOffice.organization.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.organization.domain.Department;
import com.fiveLink.linkOffice.organization.domain.DepartmentDto;
import com.fiveLink.linkOffice.organization.repository.DepartmentRepository; 

@Service
public class DepartmentService {
	private final DepartmentRepository departmentRepository; 
	
	@Autowired
	public DepartmentService(DepartmentRepository departmentRepository) {
		this.departmentRepository = departmentRepository; 
	}
	
	public List<DepartmentDto> selectBoardList(){
		List<Department> departmentList = departmentRepository.findAll();
		List<DepartmentDto> departmentDtoList = new ArrayList<DepartmentDto>();
		for(Department d : departmentList) {
			DepartmentDto dto = new DepartmentDto().toDto(d);
			departmentDtoList.add(dto);
		} 
		return departmentDtoList;
	}
}	
