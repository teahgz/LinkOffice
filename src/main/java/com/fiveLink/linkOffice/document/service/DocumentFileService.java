package com.fiveLink.linkOffice.document.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.document.domain.DocumentFile;
import com.fiveLink.linkOffice.document.domain.DocumentFileDto;
import com.fiveLink.linkOffice.document.repository.DocumentFileRepository;

@Service
public class DocumentFileService {

	private final DocumentFileRepository documentFileRepository;
	
	private static final Logger LOGGER
	= LoggerFactory.getLogger(DocumentFileService.class);
	
	@Autowired
	public DocumentFileService(DocumentFileRepository documentFileRepository) {
		this.documentFileRepository = documentFileRepository;
	}
	
	public List<DocumentFileDto> selectPersonalfileList(Long memberNo, Long folderId){
		// 파일 상태 = 0
		Long fileStatus = 0L;
		List<DocumentFile> documentFileList =
				documentFileRepository.findByMemberNoAndDocumentFolderNoAndDocumentFileStatus(memberNo, folderId, fileStatus);
		List<DocumentFileDto> documentFileDtoList = new ArrayList<DocumentFileDto>();
		for(DocumentFile d : documentFileList) {
			DocumentFileDto fileDto = new DocumentFileDto().toDto(d);
			documentFileDtoList.add(fileDto);
		}		
		
		return documentFileDtoList;
	}
	public List<DocumentFileDto> documentBinList(Long member_no){
		// 파일 상태 = 1
		Long document_file_status = 1L;
		// repository에 memberNo, file_Status를 넘겨줌 
		List<DocumentFile> documentFileList = 
				documentFileRepository.findByMemberNoAndDocumentFileStatus(member_no, document_file_status);
		List<DocumentFileDto> documentFileDtoList = new ArrayList<DocumentFileDto>();
		for(DocumentFile d : documentFileList) {
			DocumentFileDto fileDto = new DocumentFileDto().toDto(d);
			documentFileDtoList.add(fileDto);
		}		
		return documentFileDtoList;
	}
}
