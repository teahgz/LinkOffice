package com.fiveLink.linkOffice.approval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.approval.domain.ApprovalFile;

@Repository
public interface ApprovalFileRepository extends JpaRepository<ApprovalFile, Long>{

}
