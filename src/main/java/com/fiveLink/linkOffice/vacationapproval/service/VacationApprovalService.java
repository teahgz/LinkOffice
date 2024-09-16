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
	    
	    // 사원의 서명 정보를 dto에 추가
	    if (origin.getMember() != null) {
	        dto.setDigitalname(origin.getMember().getMemberNewDigitalImg());
	    }

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
	
	// 휴가 결재 기안 취소
	public VacationApproval deleteVacationApproval(VacationApprovalDto dto) {
		
		Member member = memberRepository.findByMemberNo(dto.getMember_no());
		VacationType vacationType = vacationTypeRepository.findByvacationTypeNo(dto.getVacation_type_no());
		
		VacationApproval va = dto.toEntity(member, vacationType);
		
		VacationApproval result = vacationApprovalRepository.save(va);
		
		return result;
	}
	

	
	// 사용자 전자결재 내역함 (로그인한 사용자가 결재 흐름에 있는지 조회 )
	public List<VacationApprovalFlowDto> getVacationApprovalFlowRoleByMemberNo(Long member_no){
		
		List<VacationApprovalFlow> flowList = vacationApprovalFlowRepository.findByMemberMemberNoAndRole(member_no);
		List<VacationApprovalFlowDto> flowDtoList = new ArrayList<VacationApprovalFlowDto>();
		
		for(VacationApprovalFlow vaf : flowList) {
			VacationApprovalFlowDto dto = vaf.toDto();
			flowDtoList.add(dto);
		}
		return flowDtoList;
	}
	
	// 사용자 전자결재 참조함 (로그인한 사용자가 결재 흐름에 있는지 조회)
	public List<VacationApprovalFlowDto> getVacationApprovalFlowByMemberNo(Long member_no){
		
		List<VacationApprovalFlow> flowList = vacationApprovalFlowRepository.findByMemberMemberNoAndRoleReferens(member_no);
		List<VacationApprovalFlowDto> flowDtoList = new ArrayList<VacationApprovalFlowDto>();
		
		for(VacationApprovalFlow vaf : flowList) {
			VacationApprovalFlowDto dto = vaf.toDto();
			flowDtoList.add(dto);
		}
		return flowDtoList;
	}
	
	
	public Page<VacationApprovalDto> getVacationApprovalsByNo(List<Long> vacationApprovalNos, VacationApprovalDto searchdto, Pageable pageable) {
		
		Page<VacationApproval> vacationApprovals = null;
		
		String searchText = searchdto.getSearch_text();
		
		
		if(searchText != null &&"".equals(searchText) == false) {
			int searchType = searchdto.getSearch_type();
			
			switch(searchType) {
				case 1 :
					vacationApprovals = vacationApprovalRepository.findByTitleOrNameContainingAndVacationApprovalNoIn(searchText,vacationApprovalNos, pageable);
					break;
				case 2 :
					vacationApprovals = vacationApprovalRepository.findByVacationApprovalTitleContainingAndVacationApprovalNoIn(searchText, vacationApprovalNos, pageable);
					break;
				case 3 :
					vacationApprovals = vacationApprovalRepository.findByMemberMemberNameContainingAndVacationApprovalNoIn(searchText,vacationApprovalNos, pageable);
					break;
			}
		} else {
			vacationApprovals = vacationApprovalRepository.findByVacationApprovalNoIn(vacationApprovalNos, pageable);
		}
	    
	    // VacationApprovalDto로 변환
	    List<VacationApprovalDto> approvalDtoList = vacationApprovals.stream()
	        .map(VacationApproval::toDto)
	        .collect(Collectors.toList());

	    // PageImpl을 사용하여 Page<VacationApprovalDto>를 반환
	    return new PageImpl<>(approvalDtoList, pageable, vacationApprovals.getTotalElements());
	}


	
}
