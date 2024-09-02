package com.fiveLink.linkOffice.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.member.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    Member findByMemberNumber(String memberNumber);
}
