package com.fiveLink.linkOffice.permission.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.domain.MemberPermission;

@Repository
public interface MemberPermissionRepository extends JpaRepository<MemberPermission, Long> {

    @Query("SELECT mp.memberNo FROM MemberPermission mp WHERE mp.menuPermissionNo = :menuPermissionNo")
    List<Long> findMemberNosByMenuPermissionNo(@Param("menuPermissionNo") Long menuPermissionNo);

    @Query("SELECT m, mp.memberPermissionCreateDate FROM Member m JOIN MemberPermission mp ON m.memberNo = mp.memberNo WHERE mp.menuPermissionNo IN :menuPermissionNos")
    List<Object[]> findMembersByMenuPermissionNos(@Param("menuPermissionNos") List<Long> menuPermissionNos);

}
