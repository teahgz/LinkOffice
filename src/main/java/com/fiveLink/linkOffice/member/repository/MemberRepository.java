package com.fiveLink.linkOffice.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.member.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    


    Member findByMemberNumber(String memberNumber); 
    
    // [서혜원] 조직도
    @Query("SELECT m FROM Member m WHERE m.departmentNo = :departmentNo")
    List<Member> findByDepartmentNo(Long departmentNo);

    @Query("SELECT m FROM Member m WHERE m.positionNo = :positionNo")
    List<Member> findByPositionNo(Long positionNo);

}
