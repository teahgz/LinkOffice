package com.fiveLink.linkOffice.approval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.approval.domain.Approval;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long>{

}
