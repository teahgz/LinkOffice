package com.fiveLink.linkOffice.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fiveLink.linkOffice.document.domain.DocumentFile;

@Repository
public interface DocumentFileRepository extends JpaRepository<DocumentFile, Long>{

	// 휴지통 파일  
	List<DocumentFile> findByMemberMemberNoAndDocumentFileStatus(Long memberNo, Long documentFileStatus);

	List<DocumentFile> findByMemberMemberNoAndDocumentFolderDocumentFolderNoAndDocumentFileStatus(Long memberNo, Long folderNo, Long fileStatus);

	// 폴더에 파일 찾기 
	List<DocumentFile> findByDocumentFolderDocumentFolderNoAndDocumentFileStatus(Long folderNo, Long fileStatus);
	
	// 폴더에 파일 찾기 
	@Query("SELECT f, m.memberName, d.departmentName, p.positionName " +
		       "FROM DocumentFile f " +
		       "JOIN f.documentFolder df " +  
		       "JOIN df.department d " +    
		       "JOIN f.member m " +                   
		       "JOIN m.position p " +  
		       "WHERE f.documentFolder.documentFolderNo = :folderNo " +
		       "AND f.documentFileStatus = :fileStatus")
	List<Object[]> findDocumentFileWithMemberDepartmentAndPosition(@Param("folderNo") Long folderNo, @Param("fileStatus") Long fileStatus);

	// 파일 번호로 파일 찾기 
	DocumentFile findByDocumentFileNo(Long documentFileNo);


}
