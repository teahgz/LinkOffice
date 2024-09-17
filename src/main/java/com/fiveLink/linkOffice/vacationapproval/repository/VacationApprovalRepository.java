package com.fiveLink.linkOffice.vacationapproval.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.vacationapproval.domain.VacationApproval;
import com.fiveLink.linkOffice.vacationapproval.domain.VacationApprovalFlow;


@Repository
public interface VacationApprovalRepository extends JpaRepository<VacationApproval, Long>{

	// 사원의 휴가신청함
	Page<VacationApproval> findAllByMemberMemberNo(Long memberNo, Pageable pageable);
	
	// 전체 조회
	@Query("SELECT va FROM VacationApproval va " +
			"WHERE va.member.memberNo = :memberNo " +
			"AND ((:searchText = '진행중' AND va.vacationApprovalStatus = 0) " +
			"     OR (:searchText = '완료' AND va.vacationApprovalStatus = 1) " +
			"     OR (:searchText = '반려' AND va.vacationApprovalStatus = 2) " +
			"     OR (:searchText = '취소' AND va.vacationApprovalStatus = 3)"
			+ "   OR (va.vacationApprovalTitle LIKE %:searchText%))")
	Page<VacationApproval> findByMemberMemberNoAndVacationApprovalTitleAndApprovalStatus(@Param("memberNo") Long memberNo, @Param("searchText") String searchText, Pageable pageable);

	// 제목으로 검색
	 Page<VacationApproval> findByMemberMemberNoAndVacationApprovalTitleContaining(Long memberNo, String searchText, Pageable pageable);

	 // 상태로 검색
	 @Query("SELECT va FROM VacationApproval va " +
	           "WHERE va.member.memberNo = :memberNo " +
	           "AND ((:searchText = '진행중' AND va.vacationApprovalStatus = 0) " +
	           "     OR (:searchText = '완료' AND va.vacationApprovalStatus = 1) " +
	           "     OR (:searchText = '반려' AND va.vacationApprovalStatus = 2) " +
	           "     OR (:searchText = '취소' AND va.vacationApprovalStatus = 3))")
	 Page<VacationApproval> findByMemberNoAndApprovalStatus(@Param("memberNo") Long memberNo, @Param("searchText") String searchText, Pageable pageable);
	 
	 
	 // 사원 휴가 신청 상세 조회 
	 VacationApproval findByVacationApprovalNo(Long VacatioApprovalNo);
	 
	 // 문서 번호로 조회
	 Page<VacationApproval> findByVacationApprovalNoIn(List<Long> vacationApprovalNos, Pageable pageable);
	 
	 // 제목으로 검색
	 Page<VacationApproval> findByVacationApprovalTitleContainingAndVacationApprovalNoIn(String title, List<Long> vacationApprovalNos, Pageable pageable);
	
	 // 기안자로 검색
	 Page<VacationApproval> findByMemberMemberNameContainingAndVacationApprovalNoIn(String name, List<Long> vacationApprovalNos, Pageable pageable);
	 
	 // 전체 조회
	 @Query("SELECT va FROM VacationApproval va WHERE va.vacationApprovalNo IN :vacationApprovalNos AND (va.vacationApprovalTitle LIKE %:searchText% OR va.member.memberName LIKE %:searchText%)")
	 Page<VacationApproval> findByTitleOrNameContainingAndVacationApprovalNoIn( @Param("searchText") String searchText, @Param("vacationApprovalNos") List<Long> vacationApprovalNos,Pageable pageable);
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	// [박혜선] 사원 휴가 신청 조회(근태 조회)
	 List<VacationApproval> findByMemberMemberNo(Long memberNo);
}