package com.fiveLink.linkOffice.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.member.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByMemberNumber(String memberNumber); 
    
    @Query("SELECT m, p.positionName, d.departmentName " +
            "FROM Member m " +
            "JOIN m.position p " +
            "JOIN m.department d " +
            "WHERE m.memberNumber = :memberNumber")
    List<Object[]> findMemberNumber(@Param("memberNumber") String memberNumber);
    
    // mypage 정보 조회
    @Query("SELECT m, p.positionName, d.departmentName " +
            "FROM Member m " +
            "JOIN m.position p " +
            "JOIN m.department d " +
            "WHERE m.memberNo = :memberNo")
     List<Object[]> findMemberWithDepartmentAndPosition(@Param("memberNo") Long memberNo); 

    
    // [서혜원] 부서 등록
    @Query(value = "SELECT * FROM fl_member WHERE department_no = :departmentNo", nativeQuery = true)
    List<Member> findByDepartmentNo(@Param("departmentNo") Long departmentNo);

    // [서혜원] 조직도
    @Query("SELECT m FROM Member m WHERE m.positionNo = :positionNo")
    List<Member> findByPositionNo(Long positionNo); 
    
    // [서혜원] 부서 소속 사원 여부
    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.departmentNo = :departmentNo")
    boolean existsByDepartmentNo(@Param("departmentNo") Long departmentNo);

    // [서혜원] 하위 부서 소속 사원 여부
    long countByDepartmentNo(Long departmentNo);
}
