package com.fiveLink.linkOffice.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.document.domain.DocumentFolder;

@Repository
public interface DocumentFolderRepository extends JpaRepository<DocumentFolder, Long>{
	
	List<DocumentFolder> findByMemberMemberNoAndDocumentBoxTypeAndDocumentFolderStatus(
	        Long memberNo, Long documentBoxType, Long documentFolderStatus);
	
	List<DocumentFolder> findByDepartmentDepartmentNoAndDocumentBoxTypeAndDocumentFolderStatus(
			Long departmentNo, Long documentBoxType, Long documentFolderStatus);
	
	List<DocumentFolder> findByDocumentBoxTypeAndDocumentFolderStatus(Long documentBoxType, Long documentFolderStatus);
}
