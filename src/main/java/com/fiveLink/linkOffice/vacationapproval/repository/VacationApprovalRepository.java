package com.fiveLink.linkOffice.vacationapproval.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.vacationapproval.domain.VacationApproval;


@Repository
public interface VacationApprovalRepository extends JpaRepository<VacationApproval, Long>{

	// 사원의 휴가신청함
	Page<VacationApproval> findAllByMemberMemberNo(Long memberNo, Pageable pageable);
	
	// 제목으로 검색
	 Page<VacationApproval> findByMemberMemberNoAndVacationApprovalTitleContaining(Long memberNo, String searchText, Pageable pageable);
}
