package com.fiveLink.linkOffice.approval.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.approval.domain.Approval;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long>{
	// 공통
	Page<Approval> findByMemberMemberNoAndApprovalStatusIn(Long memberNo, List<Integer> approvalStatus, Pageable sortedPageable);
	
	Page<Approval> findByMemberMemberNoAndApprovalStatusInAndApprovalTitleContaining(Long memberNo, List<Integer> approvalStatus, String searchText, Pageable sortedPageable);
	
	// 진행
	
	@Query("SELECT app FROM Approval app "+
			"WHERE app.member.memberNo = :memberNo "+
			"AND app.approvalStatus IN (0,1) "+
			"AND ((:searchText = '진행중' OR :searchText = '진행' AND app.approvalStatus = 0) "+
			"     OR (:searchText = '완료' AND app.approvalStatus = 1) "+
			"     OR (app.approvalTitle LIKE %:searchText%))")
	Page<Approval> findByMemberMemberNoAndApprovalStatusAndApprovalTitle(@Param("memberNo") Long memberNo, @Param("searchText") String searchText, Pageable sortedPageable);
	
	@Query("SELECT app FROM Approval app "+
			"WHERE app.member.memberNo = :memberNo "+
			"AND app.approvalStatus IN (0,1) "+
			"AND ((:searchText = '진행중' OR :searchText = '진행' AND app.approvalStatus = 0) "+
			"     OR (:searchText = '완료' AND app.approvalStatus = 1))")
	Page<Approval> findByMemberMemberNoAndApprovalStatus(@Param("memberNo") Long memberNo, @Param("searchText") String searchText, Pageable sortedPageable);
	
	// 반려
	
	@Query("SELECT app FROM Approval app "+
			"WHERE app.member.memberNo = :memberNo "+
			"AND app.approvalStatus IN (2,3) "+
			"AND ((:searchText = '반려' AND app.approvalStatus = 2) "+
			"     OR (:searchText = '기안취소' OR :searchText = '취소' AND app.approvalStatus = 3) "+
			"     OR (app.approvalTitle LIKE %:searchText%))")
	Page<Approval> findByMemberMemberNoAndApprovalStatusAndApprovalTitleReject(@Param("memberNo") Long memberNo, @Param("searchText") String searchText, Pageable sortedPageable);
	
	
	@Query("SELECT app FROM Approval app "+
			"WHERE app.member.memberNo = :memberNo "+
			"AND app.approvalStatus IN (2,3) "+
			"AND ((:searchText = '반려' AND app.approvalStatus = 2) "+
			"     OR (:searchText = '기안취소' OR :searchText = '취소' AND app.approvalStatus = 3))")
	Page<Approval> findByMemberMemberNoAndApprovalStatusReject(@Param("memberNo") Long memberNo, @Param("searchText") String searchText, Pageable sortedPageable);

	// 진행함 상세 조회
	Approval findByApprovalNo(Long ApprovalNo);
}
