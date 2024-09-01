package com.fiveLink.linkOffice.document.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.document.domain.DocumentFolder;
import com.fiveLink.linkOffice.document.domain.DocumentFolderDto;
import com.fiveLink.linkOffice.document.repository.DocumentFolderRepository;

@Service
public class DocumentFolderService {

private final DocumentFolderRepository documentFolderRepository;
	
	@Autowired
	public DocumentFolderService(DocumentFolderRepository documentFolderRepository) {
		this.documentFolderRepository = documentFolderRepository;
	}
	// 개인 문서함 
	public List<DocumentFolderDto> selectPersonalFolderList(Long member_no) {
		// 문서함 타입 = 0(개인)
		Long document_box_type = 0L;
		// 폴더 상태 = 0
		Long document_folder_status = 0L;
		// repository에 memberNo, documentBoxType, folderStatus를 넘겨줌 
		List<DocumentFolder> documentFolderList 
			= documentFolderRepository.findBymemberNoAnddocumentBoxTypeAnddocumentFolderStatus(member_no, document_box_type, document_folder_status);
		List<DocumentFolderDto> documentFolderDtoList = new ArrayList<DocumentFolderDto>();
		for(DocumentFolder d : documentFolderList) {
			DocumentFolderDto folderDto = new DocumentFolderDto().toDto(d);
			documentFolderDtoList.add(folderDto);
		}
		return documentFolderDtoList;
	}
	// 부서 문서함 
	public List<DocumentFolderDto> selectDepartmentFolderList(Long department_no) {
		// 문서함 타입 = 1(부서)
		Long document_box_type = 1L;
		// 폴더 상태 = 0
		Long document_folder_status = 0L;
		// repository에 departmentNo, documentBoxType, folderStatus를 넘겨줌
		List<DocumentFolder> documentFolderList 
			= documentFolderRepository.findBydepartmentNoAnddocumentBoxTypeAnddocumentFolderStatus(
					department_no, document_box_type, document_folder_status);
		List<DocumentFolderDto> documentFolderDtoList = new ArrayList<DocumentFolderDto>();
		for(DocumentFolder d : documentFolderList) {
			DocumentFolderDto folderDto = new DocumentFolderDto().toDto(d);
			documentFolderDtoList.add(folderDto);
		}
		return documentFolderDtoList;
	}
	// 사내 문서함 
	public List<DocumentFolderDto> selectCompanyFolderList(Long document_box_type){
		// 폴더 상태 = 0
		Long document_folder_status = 0L;
		// repository에 documentBoxType과 folderStatus를 넘겨줌 
		List<DocumentFolder> documentFolderList 
			= documentFolderRepository.findBydocumentBoxType(document_box_type, document_folder_status);
		List<DocumentFolderDto> documentFolderDtoList = new ArrayList<DocumentFolderDto>();
		for(DocumentFolder d : documentFolderList) {
			DocumentFolderDto folderDto = new DocumentFolderDto().toDto(d);
			documentFolderDtoList.add(folderDto);
		}
		return documentFolderDtoList;
	}
}
