package com.fiveLink.linkOffice.approval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.approval.domain.ApprovalForm;
import com.fiveLink.linkOffice.approval.domain.ApprovalFormDto;

@Repository
public interface ApprovalFormRepository extends JpaRepository<ApprovalForm, Long>{
	// 전체 양식 (상태 1 제외 조회)
	List<ApprovalForm> findAllByApprovalFormStatusNot(Long approvalFormStatus);
}
