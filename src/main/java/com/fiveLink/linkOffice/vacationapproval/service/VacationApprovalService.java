package com.fiveLink.linkOffice.vacationapproval.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.repository.MemberRepository;
import com.fiveLink.linkOffice.vacation.domain.VacationType;
import com.fiveLink.linkOffice.vacation.repository.VacationTypeRepository;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApproval;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalDto;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFile;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFileDto;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFlow;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFlowDto;
import com.fiveLink.linkOffice.vacationapproval.repository.VacationApprovalFileRepository;
import com.fiveLink.linkOffice.vacationapproval.repository.VacationApprovalFlowRepository;
import com.fiveLink.linkOffice.vacationapproval.repository.VacationApprovalRepository;

@Service
public class VacationApprovalService {
	
	private final VacationApprovalRepository vacationApprovalRepository;
	private final MemberRepository memberRepository;
	private final VacationTypeRepository vacationTypeRepository;
	private final VacationApprovalFileRepository vacationApprovalFileRepository;
	private final VacationApprovalFlowRepository vacationApprovalFlowRepository;
	
	@Autowired
	public VacationApprovalService(VacationApprovalRepository vacationApprovalRepository,MemberRepository memberRepository,VacationTypeRepository vacationTypeRepository, VacationApprovalFileRepository vacationApprovalFileRepository, VacationApprovalFlowRepository vacationApprovalFlowRepository) {
        this.vacationApprovalRepository = vacationApprovalRepository;
        this.memberRepository = memberRepository;
        this.vacationTypeRepository = vacationTypeRepository;
        this.vacationApprovalFileRepository = vacationApprovalFileRepository;
        this.vacationApprovalFlowRepository = vacationApprovalFlowRepository;
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

	    List<VacationApprovalFile> files = vacationApprovalFileRepository.findByVacationApproval(origin);
	    List<VacationApprovalFlow> flows = vacationApprovalFlowRepository.findByVacationApproval(origin);
	    
	    List<VacationApprovalFileDto> fileDtos = files.stream()
	        .map(VacationApprovalFile::toDto)
	        .collect(Collectors.toList());
	    
	    List<VacationApprovalFlowDto> flowsDtos = flows.stream()
		        .map(VacationApprovalFlow::toDto)
		        .collect(Collectors.toList());
	    
	    dto.setFiles(fileDtos);
	    dto.setFlows(flowsDtos);
	    return dto;
	}

	
	// 사용자 휴가 신청 (파일 X)
	public VacationApproval createVacationApproval(VacationApprovalDto vappdto, List<VacationApprovalFlowDto> approvalFlowDtos) {
	    
	    Member member = memberRepository.findByMemberNo(vappdto.getMember_no());
	    
	    VacationType vacationType = vacationTypeRepository.findByvacationTypeNo(vappdto.getVacation_type_no());
	    
	    VacationApproval vapp = vappdto.toEntity(member, vacationType);
	    VacationApproval savedVapp = vacationApprovalRepository.save(vapp); 
	    
	    for (VacationApprovalFlowDto flowDto : approvalFlowDtos) {
	    	
	        Long approverMemberNo = flowDto.getMember_no();
	        
	        Member memberFlow = memberRepository.findByMemberNo(approverMemberNo);
	    	
	    	VacationApprovalFlow vaf = flowDto.toEntity(vapp, memberFlow);
	    	
	    	vacationApprovalFlowRepository.save(vaf);
	    }
	    
	    return savedVapp; 
	}

	// 사용자 휴가 신청 (파일 O)
	public VacationApproval createVacationApprovalFile(VacationApprovalDto vappdto, VacationApprovalFileDto vaFiledto, List<VacationApprovalFlowDto> approvalFlowDtos) {
		
		Member member = memberRepository.findByMemberNo(vappdto.getMember_no());
		VacationType vacationType = vacationTypeRepository.findByvacationTypeNo(vappdto.getVacation_type_no());
		
		VacationApproval vapp = vappdto.toEntity(member, vacationType);
		
		VacationApproval savedVapp = vacationApprovalRepository.save(vapp);
	    for (VacationApprovalFlowDto flowDto : approvalFlowDtos) {
	    	
	    	Long approverMemberNo = flowDto.getMember_no();
	        
	        Member memberFlow = memberRepository.findByMemberNo(approverMemberNo);
	        
	    	VacationApprovalFlow vaf = flowDto.toEntity(vapp, memberFlow);
	    	
	    	vacationApprovalFlowRepository.save(vaf);
	    }
	    
		 if (vaFiledto != null) {
		        VacationApprovalFile vaFile = vaFiledto.toEntity(savedVapp);
		        vacationApprovalFileRepository.save(vaFile); 
		    }
		return vacationApprovalRepository.save(vapp);
		
	}
}
