package com.fiveLink.linkOffice.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.document.domain.DocumentFolder;

@Repository
public interface DocumentFolderRepository extends JpaRepository<DocumentFolder, Long>{
	
	// 개인 폴더 리스트 
	List<DocumentFolder> findByMemberMemberNoAndDocumentBoxTypeAndDocumentFolderStatus(
	        Long memberNo, Long documentBoxType, Long documentFolderStatus);
	
	// 부서 폴더 리스트 
	List<DocumentFolder> findByDepartmentDepartmentNoAndDocumentBoxTypeAndDocumentFolderStatus(
			Long departmentNo, Long documentBoxType, Long documentFolderStatus);
	
	// 사내 폴더 리스트
	List<DocumentFolder> findByDocumentBoxTypeAndDocumentFolderStatus(Long documentBoxType, Long documentFolderStatus);

	// folderNo로 폴더 찾기
	DocumentFolder findByDocumentFolderNo(Long documentFolderNo);
}
