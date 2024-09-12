package com.fiveLink.linkOffice.vacationapproval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFile;

@Repository
public interface VacationApprovalFileRepository extends JpaRepository<VacationApprovalFile, Long>{

}
