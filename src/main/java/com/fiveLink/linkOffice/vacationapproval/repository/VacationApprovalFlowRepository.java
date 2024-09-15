package com.fiveLink.linkOffice.vacationapproval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFlow;

@Repository
public interface VacationApprovalFlowRepository extends JpaRepository<VacationApprovalFlow, Long>{

}
