package com.fiveLink.linkOffice.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.document.domain.DocumentFile;

@Repository
public interface DocumentFileRepository extends JpaRepository<DocumentFile, Long>{

	List<DocumentFile> findByMemberMemberNoAndDocumentFileStatus(Long memberNo, Long documentFileStatus);
	
	List<DocumentFile> findByMemberMemberNoAndDocumentFolderDocumentFolderNoAndDocumentFileStatus(Long memberNo, Long folderNo, Long fileStatus);

	List<DocumentFile> findByDocumentFolderDocumentFolderNoAndDocumentFileStatus(Long folderNo, Long fileStatus);
}
