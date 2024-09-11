package com.fiveLink.linkOffice.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fiveLink.linkOffice.notice.domain.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    
    Notice findBynoticeNo(Long notice_no);

    @Query("SELECT m.memberName FROM Member m WHERE m.memberNumber = :memberNumber")
    String findMemberNameByMemberNumber(@Param("memberNumber") String memberNumber);
}
