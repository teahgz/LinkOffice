package com.fiveLink.linkOffice.approval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.approval.domain.ApprovalFlow;

@Repository
public interface ApprovalFlowRepository extends JpaRepository<ApprovalFlow, Long> {

}
