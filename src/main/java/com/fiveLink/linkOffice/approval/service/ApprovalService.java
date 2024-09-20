package com.fiveLink.linkOffice.approval.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.approval.domain.Approval;
import com.fiveLink.linkOffice.approval.domain.ApprovalDto;
import com.fiveLink.linkOffice.approval.domain.ApprovalFile;
import com.fiveLink.linkOffice.approval.domain.ApprovalFileDto;
import com.fiveLink.linkOffice.approval.domain.ApprovalFlow;
import com.fiveLink.linkOffice.approval.domain.ApprovalFlowDto;
import com.fiveLink.linkOffice.approval.repository.ApprovalFileRepository;
import com.fiveLink.linkOffice.approval.repository.ApprovalFlowRepository;
import com.fiveLink.linkOffice.approval.repository.ApprovalRepository;
import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.repository.MemberRepository;

@Service
public class ApprovalService {

	private final MemberRepository memberRepository;
	private final ApprovalRepository approvalRepository;
	private final ApprovalFlowRepository approvalFlowRepository;
	private final ApprovalFileRepository approvalFileRepository;
	
	@Autowired
	public ApprovalService(MemberRepository memberRepository, ApprovalRepository approvalRepository, ApprovalFlowRepository approvalFlowRepository, ApprovalFileRepository approvalFileRepository) {
        this.memberRepository = memberRepository;
        this.approvalRepository = approvalRepository;
        this.approvalFlowRepository = approvalFlowRepository;
        this.approvalFileRepository = approvalFileRepository;
    }
	
	// 사용자 결재 신청 (파일 O)
	public Approval createApprovalFile(ApprovalDto appdto, ApprovalFileDto filedto, List<ApprovalFlowDto> flowdto) {
		Member member = memberRepository.findByMemberNo(appdto.getMember_no());
		
		Approval app = appdto.toEntity(member);
			
			Approval savedApp = approvalRepository.save(app);
			for(ApprovalFlowDto appflowdto : flowdto) {

		    	Long approverMemberNo = appflowdto.getMember_no();
		        
		        Member memberFlow = memberRepository.findByMemberNo(approverMemberNo);
		        
		    	ApprovalFlow vaf = appflowdto.toEntity(savedApp, memberFlow);
		    	
		    	approvalFlowRepository.save(vaf);
		    }
		    
			 if (filedto != null) {
			       	ApprovalFile vaFile = filedto.toEntity(savedApp);
			        approvalFileRepository.save(vaFile); 
			    }
		return approvalRepository.save(app);
	}
	
	// 사용자 결재 신청 (파일 x)
	public Approval createApproval(ApprovalDto appdto, List<ApprovalFlowDto> flowdto) {
		Member member = memberRepository.findByMemberNo(appdto.getMember_no());
		
		Approval app = appdto.toEntity(member);
			
			Approval savedApp = approvalRepository.save(app);
			for(ApprovalFlowDto appflowdto : flowdto) {

		    	Long approverMemberNo = appflowdto.getMember_no();
		        
		        Member memberFlow = memberRepository.findByMemberNo(approverMemberNo);
		        
		    	ApprovalFlow vaf = appflowdto.toEntity(savedApp, memberFlow);
		    	
		    	approvalFlowRepository.save(vaf);
		    }

		return approvalRepository.save(app);
	}
	
	// 결재 진행함
	public Page<ApprovalDto> getAllApproval(Long member_no, ApprovalDto searchdto, Pageable sortedPageable){
		
		Page<Approval> approvals = null;
        List<ApprovalDto> approvalDtoList = new ArrayList<ApprovalDto>();
			
			List<Integer> statusList = Arrays.asList(0, 1); 
			System.out.println(statusList);
			String searchText = searchdto.getSearch_text();
			if(searchText != null && "".equals(searchText) == false) {
				int searchType = searchdto.getSearch_type();
				
				switch(searchType) {
					case 1 : 
						approvals = approvalRepository.findByMemberMemberNoAndApprovalStatusAndApprovalTitle(member_no, searchText, sortedPageable);
						break;
					case 2 :
						approvals = approvalRepository.findByMemberMemberNoAndApprovalStatusInAndApprovalTitleContaining(member_no, statusList, searchText, sortedPageable);
						break;
					case 3 :
						approvals = approvalRepository.findByMemberMemberNoAndApprovalStatus(member_no,searchText ,sortedPageable);
						break;						
				}
			}else {
				approvals = approvalRepository.findByMemberMemberNoAndApprovalStatusIn(member_no, statusList, sortedPageable);
			}

	        for(Approval app : approvals) {
	        	System.out.println(app);
	        	ApprovalDto dto = app.toDto();
	        	approvalDtoList.add(dto);
	        }
        return new PageImpl<>(approvalDtoList, sortedPageable, approvals.getTotalElements());
	}
	
	// 결재 반려함
	public Page<ApprovalDto> getAllReject(Long member_no, ApprovalDto searchdto, Pageable sortedPageable){
		
		Page<Approval> approvals = null;
        List<ApprovalDto> approvalDtoList = new ArrayList<ApprovalDto>();
			
			List<Integer> statusList = Arrays.asList(2, 3); 
			System.out.println(statusList);
			String searchText = searchdto.getSearch_text();
			if(searchText != null && "".equals(searchText) == false) {
				int searchType = searchdto.getSearch_type();
				
				switch(searchType) {
					case 1 : 
						approvals = approvalRepository.findByMemberMemberNoAndApprovalStatusAndApprovalTitleReject(member_no, searchText, sortedPageable);
						break;
					case 2 :
						approvals = approvalRepository.findByMemberMemberNoAndApprovalStatusInAndApprovalTitleContaining(member_no, statusList, searchText, sortedPageable);
						break;
					case 3 :
						approvals = approvalRepository.findByMemberMemberNoAndApprovalStatusReject(member_no,searchText ,sortedPageable);
						break;	
				}
			}else {
				approvals = approvalRepository.findByMemberMemberNoAndApprovalStatusIn(member_no, statusList, sortedPageable);
			}

	        for(Approval app : approvals) {
	        	System.out.println(app);
	        	ApprovalDto dto = app.toDto();
	        	approvalDtoList.add(dto);
	        }
        return new PageImpl<>(approvalDtoList, sortedPageable, approvals.getTotalElements());
	}
	
}
