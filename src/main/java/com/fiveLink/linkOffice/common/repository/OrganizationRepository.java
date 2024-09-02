package com.fiveLink.linkOffice.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.fiveLink.linkOffice.member.domain.Member;

public interface OrganizationRepository extends CrudRepository<Member, Long> {
    
    // 부서 번호에 따른 멤버 조회
    @Query("SELECT m FROM Member m WHERE m.departmentNo = :departmentNo")
    List<Member> findByDepartmentNo(Long departmentNo);

    // 직위 번호에 따른 멤버 조회
    @Query("SELECT m FROM Member m WHERE m.positionNo = :positionNo")
    List<Member> findByPositionNo(Long positionNo);
}
