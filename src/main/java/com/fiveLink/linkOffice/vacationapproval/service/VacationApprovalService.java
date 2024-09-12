package com.fiveLink.linkOffice.vacationapproval.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.vacationapproval.domain.VacationApproval;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalDto;
import com.fiveLink.linkOffice.vacationapproval.repository.VacationApprovalRepository;

@Service
public class VacationApprovalService {
	
	private final VacationApprovalRepository vacationApprovalRepository;
	
	public VacationApprovalService(VacationApprovalRepository vacationApprovalRepository) {
        this.vacationApprovalRepository = vacationApprovalRepository;
    }
	
	// 사용자 휴가신청함 목록 조회
	public Page<VacationApprovalDto> getVacationApprovalByNo(Long memberNo,VacationApprovalDto searchdto, Pageable pageable) {
    	
        Page<VacationApproval> forms = null;
        
        String searchText = searchdto.getSearch_text();
        if(searchText != null && "".equals(searchText) == false) {
        	int searchType = searchdto.getSearch_type();
        	
        	switch(searchType) {
        	// 전체 검색
        		case 1 :
        			forms = vacationApprovalRepository.findByMemberMemberNoAndVacationApprovalTitleAndApprovalStatus(memberNo,searchText, pageable);;
        			break;
        	// 제목 검색
        		case 2 :
        			forms = vacationApprovalRepository.findByMemberMemberNoAndVacationApprovalTitleContaining(memberNo,searchText, pageable);
        			break;
        	// 상태 검색
        		case 3 :
        			forms = vacationApprovalRepository.findByMemberNoAndApprovalStatus(memberNo,searchText, pageable);;
        			break;
        	}
        } else {
        	forms = vacationApprovalRepository.findAllByMemberMemberNo(memberNo,pageable);
        }
        
        List<VacationApprovalDto> approvalApprovalDtoList = new ArrayList<VacationApprovalDto>();
        for(VacationApproval va : forms) {
        	VacationApprovalDto dto = va.toDto();
        	approvalApprovalDtoList.add(dto);
        }
        
        return new PageImpl<>(approvalApprovalDtoList, pageable, forms.getTotalElements());
    }
	
	// 사용자 휴가신청함 상세 조회 
	
	public VacationApprovalDto selectVacationApprovalOne(Long vacationApprovalNo) {
		VacationApproval origin = vacationApprovalRepository.findByVacationApprovalNo(vacationApprovalNo);
		VacationApprovalDto dto = origin.toDto();
		return dto;
	}
}
