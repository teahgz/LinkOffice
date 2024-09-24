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
	
	
	// 참조함 조회

	
	@Query(value = "SELECT ap.approval_no, " +
	        "ap.member_no, " +
	        "ap.approval_title, " +
	        "ap.approval_effective_date, " +
	        "ap.approval_content, " +
	        "ap.approval_status, " +
	        "ap.approval_create_date AS approval_date, " +  
	        "ap.approval_update_date, " +
	        "ap.approval_cancel_reason, " +
	        "af.approval_flow_role, " +
	        "'APPROVAL' AS approval_type " + 
	        "FROM fl_approval ap " +
	        "JOIN fl_approval_flow af ON ap.approval_no = af.approval_no " +
	        "WHERE af.member_no = :loggedInMemberNo " + 
	        "AND (af.approval_flow_role = 0) " +
	        "UNION ALL " +
	        "SELECT va.vacation_approval_no, " +
	        "va.member_no, " +
	        "va.vacation_approval_title, " +
	        "NULL AS approval_effective_date, " +
	        "va.vacation_approval_content, " +
	        "va.vacation_approval_status, " +
	        "va.vacation_approval_create_date AS approval_date, " + 
	        "va.vacation_approval_update_date, " +
	        "va.vacation_approval_cancel_reason, " +
	        "vaf.vacation_approval_flow_role AS approval_flow_role, " +
	        "'VACATION' AS approval_type " +  
	        "FROM fl_vacation_approval va " +
	        "JOIN fl_vacation_approval_flow vaf ON va.vacation_approval_no = vaf.vacation_approval_no " +
	        "WHERE vaf.member_no = :loggedInMemberNo " + 
	        "AND (vaf.vacation_approval_flow_role = 0) " +
	        "ORDER BY approval_date DESC", 
	    nativeQuery = true)
	List<Object[]> findAllApprovalReferences(@Param("loggedInMemberNo") Long memberNo);

	/*
	 * // 제목 검색 (참조함)
	 * 
	 * @Query(value = "SELECT approval_no, " + "member_no, " + "approval_title, " +
	 * "approval_effective_date, " + "approval_content, " + "approval_status, " +
	 * "approval_create_date, " + "approval_update_date, " +
	 * "approval_cancel_reason " + "FROM fl_approval ap " + "WHERE EXISTS (" +
	 * "    SELECT 1 " + "    FROM fl_approval_flow af " +
	 * "    WHERE ap.approval_no = af.approval_no " +
	 * "          AND af.member_no = :loggedInMemberNo " +
	 * "          AND af.approval_flow_role = 0) " +
	 * "AND (approval_title LIKE %:searchText%) " + "UNION ALL " +
	 * "SELECT vacation_approval_no, " + "member_no, " + "vacation_approval_title, "
	 * + "NULL AS approval_effective_date, " + "vacation_approval_content, " +
	 * "vacation_approval_status, " + "vacation_approval_create_date, " +
	 * "vacation_approval_update_date, " + "vacation_approval_cancel_reason  " +
	 * "FROM fl_vacation_approval va " + "WHERE EXISTS (" + "    SELECT 1 " +
	 * "    FROM fl_vacation_approval_flow vaf " +
	 * "    WHERE va.vacation_approval_no = vaf.vacation_approval_no " +
	 * "          AND vaf.member_no = :loggedInMemberNo " +
	 * "          AND vaf.vacation_approval_flow_role = 0) " +
	 * "AND (vacation_approval_title LIKE %:searchText%)", nativeQuery = true)
	 * List<Object[]> findAllApprovalReferencesTitle(@Param("loggedInMemberNo") Long
	 * memberNo, @Param("searchText") String searchText);
	 * 
	 * // 상태 검색(참조함)
	 * 
	 * @Query(value = "SELECT approval_no, " + "member_no, " + "approval_title, " +
	 * "approval_effective_date, " + "approval_content, " + "approval_status, " +
	 * "approval_create_date, " + "approval_update_date, " +
	 * "approval_cancel_reason " + "FROM fl_approval ap " + "WHERE EXISTS (" +
	 * "    SELECT 1 " + "    FROM fl_approval_flow af " +
	 * "    WHERE ap.approval_no = af.approval_no " +
	 * "          AND af.member_no = :loggedInMemberNo " +
	 * "          AND af.approval_flow_role = 0) " +
	 * "AND ((:searchText = '진행중' OR :searchText = '진행' AND approval_status = 0) " +
	 * "     OR (:searchText = '완료' AND approval_status = 1) " +
	 * "     OR (:searchText = '반려' AND approval_status = 2) " +
	 * "     OR (:searchText = '취소' AND approval_status = 3)) " + "UNION ALL " +
	 * "SELECT vacation_approval_no, " + "member_no, " + "vacation_approval_title, "
	 * + "NULL AS approval_effective_date, " + "vacation_approval_content, " +
	 * "vacation_approval_status, " + "vacation_approval_create_date, " +
	 * "vacation_approval_update_date, " + "vacation_approval_cancel_reason  " +
	 * "FROM fl_vacation_approval va " + "WHERE EXISTS (" + "    SELECT 1 " +
	 * "    FROM fl_vacation_approval_flow vaf " +
	 * "    WHERE va.vacation_approval_no = vaf.vacation_approval_no " +
	 * "          AND vaf.member_no = :loggedInMemberNo " +
	 * "          AND vaf.vacation_approval_flow_role = 0) " +
	 * "AND ((:searchText = '진행중' OR :searchText = '진행' AND va.vacation_approval_status = 0) "
	 * + "     OR (:searchText = '완료' AND va.vacation_approval_status = 1) " +
	 * "     OR (:searchText = '반려' AND va.vacation_approval_status = 2) " +
	 * "     OR (:searchText = '취소' AND va.vacation_approval_status = 3))",
	 * nativeQuery = true) List<Object[]>
	 * findAllApprovalReferencesStatus(@Param("loggedInMemberNo") Long memberNo,
	 * 
	 * @Param("searchText") String searchText);
	 * 
	 * 
	 * // 전체 검색(참조함)
	 * 
	 * @Query(value = "SELECT approval_no, " + "member_no, " + "approval_title, " +
	 * "approval_effective_date, " + "approval_content, " + "approval_status, " +
	 * "approval_create_date, " + "approval_update_date, " +
	 * "approval_cancel_reason " + "FROM fl_approval ap " + "WHERE EXISTS (" +
	 * "    SELECT 1 " + "    FROM fl_approval_flow af " +
	 * "    WHERE ap.approval_no = af.approval_no " +
	 * "          AND af.member_no = :loggedInMemberNo " +
	 * "          AND af.approval_flow_role = 0) " +
	 * "AND (approval_title LIKE %:searchText% " +
	 * "     OR (:searchText = '진행중' OR :searchText = '진행' AND approval_status = 0) "
	 * + "     OR (:searchText = '완료' AND approval_status = 1) " +
	 * "     OR (:searchText = '반려' AND approval_status = 2) " +
	 * "     OR (:searchText = '취소' AND approval_status = 3)) " + "UNION ALL " +
	 * "SELECT vacation_approval_no, " + "member_no, " + "vacation_approval_title, "
	 * + "NULL AS approval_effective_date, " + "vacation_approval_content, " +
	 * "vacation_approval_status, " + "vacation_approval_create_date, " +
	 * "vacation_approval_update_date, " + "vacation_approval_cancel_reason  " +
	 * "FROM fl_vacation_approval va " + "WHERE EXISTS (" + "    SELECT 1 " +
	 * "    FROM fl_vacation_approval_flow vaf " +
	 * "    WHERE va.vacation_approval_no = vaf.vacation_approval_no " +
	 * "          AND vaf.member_no = :loggedInMemberNo " +
	 * "          AND vaf.vacation_approval_flow_role = 0) " +
	 * "AND (vacation_approval_title LIKE %:searchText% " +
	 * "     OR (:searchText = '진행중' OR :searchText = '진행' AND va.vacation_approval_status = 0) "
	 * + "     OR (:searchText = '완료' AND va.vacation_approval_status = 1) " +
	 * "     OR (:searchText = '반려' AND va.vacation_approval_status = 2) " +
	 * "     OR (:searchText = '취소' AND va.vacation_approval_status = 3))",
	 * nativeQuery = true) List<Object[]>
	 * findAllApprovalReferencesTitleAndStatus(@Param("loggedInMemberNo") Long
	 * memberNo,
	 * 
	 * @Param("searchText") String searchText);
	 */


	// 내역함 조회
	@Query(value = "SELECT ap.approval_no, " +
	        "ap.member_no, " +
	        "ap.approval_title, " +
	        "ap.approval_effective_date, " +
	        "ap.approval_content, " +
	        "ap.approval_status, " +
	        "ap.approval_create_date AS approval_date, " +  
	        "ap.approval_update_date, " +
	        "ap.approval_cancel_reason, " +
	        "af.approval_flow_role, " +
	        "'APPROVAL' AS approval_type " + 
	        "FROM fl_approval ap " +
	        "JOIN fl_approval_flow af ON ap.approval_no = af.approval_no " +
	        "WHERE af.member_no = :loggedInMemberNo " + 
	        "AND (af.approval_flow_role = 1 OR af.approval_flow_role = 2) " +
	        "AND (af.approval_flow_status != 0) " +
	        "UNION ALL " +
	        "SELECT va.vacation_approval_no, " +
	        "va.member_no, " +
	        "va.vacation_approval_title, " +
	        "NULL AS approval_effective_date, " +
	        "va.vacation_approval_content, " +
	        "va.vacation_approval_status, " +
	        "va.vacation_approval_create_date AS approval_date, " + 
	        "va.vacation_approval_update_date, " +
	        "va.vacation_approval_cancel_reason, " +
	        "vaf.vacation_approval_flow_role AS approval_flow_role, " +
	        "'VACATION' AS approval_type " +  
	        "FROM fl_vacation_approval va " +
	        "JOIN fl_vacation_approval_flow vaf ON va.vacation_approval_no = vaf.vacation_approval_no " +
	        "WHERE vaf.member_no = :loggedInMemberNo " + 
	        "AND (vaf.vacation_approval_flow_role = 1 OR vaf.vacation_approval_flow_role = 2) " +
	        "AND (vaf.vacation_approval_flow_status != 0) " +
	        "ORDER BY approval_date DESC", 
	    nativeQuery = true)
	List<Object[]> findAllApprovalHistory(@Param("loggedInMemberNo") Long memberNo);

	// home 내역함
	@Query(value = "SELECT COUNT(*) FROM (" +
	        "SELECT ap.approval_no " +
	        "FROM fl_approval ap " +
	        "JOIN fl_approval_flow af ON ap.approval_no = af.approval_no " +
	        "WHERE af.member_no = :loggedInMemberNo " +
	        "AND (af.approval_flow_role = 1 OR af.approval_flow_role = 2) " +
	        "AND (af.approval_flow_status != 0) " +
	        "UNION ALL " +
	        "SELECT va.vacation_approval_no " +
	        "FROM fl_vacation_approval va " +
	        "JOIN fl_vacation_approval_flow vaf ON va.vacation_approval_no = vaf.vacation_approval_no " +
	        "WHERE vaf.member_no = :loggedInMemberNo " +
	        "AND (vaf.vacation_approval_flow_role = 1 OR vaf.vacation_approval_flow_role = 2) " +
	        "AND (vaf.vacation_approval_flow_status != 0) " +
	        ") AS combined", nativeQuery = true)
	long countApprovalHistory(@Param("loggedInMemberNo") Long memberNo);
	
	// home 참조함
	@Query(value = "SELECT COUNT(*) FROM (" +
	        "    SELECT approval_no " +
	        "    FROM fl_approval ap " +
	        "    WHERE EXISTS (" +
	        "        SELECT 1 " +
	        "        FROM fl_approval_flow af " +
	        "        WHERE ap.approval_no = af.approval_no " +
	        "              AND af.member_no = :loggedInMemberNo " + 
	        "              AND af.approval_flow_role = 0) " +
	        "    UNION ALL " +
	        "    SELECT vacation_approval_no " +
	        "    FROM fl_vacation_approval va " +
	        "    WHERE EXISTS (" +
	        "        SELECT 1 " +
	        "        FROM fl_vacation_approval_flow vaf " +
	        "        WHERE va.vacation_approval_no = vaf.vacation_approval_no " +
	        "              AND vaf.member_no = :loggedInMemberNo " + 
	        "              AND vaf.vacation_approval_flow_role = 0)" +
	        ") AS combined", nativeQuery = true)
	long countApprovalReferences(@Param("loggedInMemberNo") Long memberNo);

	// home 진행함 
	@Query("SELECT COUNT(a) FROM Approval a " +
		       "JOIN a.member m " +
		       "WHERE m.memberNo = :loggedInMemberNo " +
		       "AND a.approvalStatus IN :approvalStatus")
		long countApprovalProgress(@Param("loggedInMemberNo") Long memberNo, @Param("approvalStatus") List<Integer> approvalStatus);

}
