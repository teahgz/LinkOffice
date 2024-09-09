package com.fiveLink.linkOffice.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.document.domain.DocumentFile;

@Repository
public interface DocumentFileRepository extends JpaRepository<DocumentFile, Long>{

	List<DocumentFile> findByMemberNoAndDocumentFileStatus(Long memberNo, Long documentFileStatus);
}
