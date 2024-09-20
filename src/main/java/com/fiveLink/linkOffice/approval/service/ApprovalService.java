package com.fiveLink.linkOffice.approval.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
}
