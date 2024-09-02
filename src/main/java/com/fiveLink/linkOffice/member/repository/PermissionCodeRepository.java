package com.fiveLink.linkOffice.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.member.domain.PermissionCode;

@Repository
public interface PermissionCodeRepository extends JpaRepository<PermissionCode, Long>{
	@Query("SELECT pc.permissionCodeName FROM PermissionCode pc "+
			"JOIN MenuPermission mp ON pc.permissionCodeNo = mp.permissionCodeNo "+
			"JOIN MemberPermission bp ON mp.menuPermissionNo = bp.menuPermissionNo "+
			"WHERE bp.memberNo = :memberNo")
	List<String> findPermissionsByMemberNo(@Param("memberNo") Long memberNo);
}
