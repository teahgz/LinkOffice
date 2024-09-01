package com.fiveLink.linkOffice.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiveLink.linkOffice.document.domain.DocumentFolder;

public interface DocumentFolderRepository extends JpaRepository<DocumentFolder, Long>{
	
	List<DocumentFolder> findBymemberNoAnddocumentBoxTypeAnddocumentFolderStatus(
			Long memberNo, Long documentBoxType, Long documentFolderStatus);
	
	List<DocumentFolder> findBydepartmentNoAnddocumentBoxTypeAnddocumentFolderStatus(
			Long departmentNo, Long documentBoxType, Long documentFolderStatus);
	
	List<DocumentFolder> findBydocumentBoxType(Long documentBoxType, Long documentFolderStatus);
}
