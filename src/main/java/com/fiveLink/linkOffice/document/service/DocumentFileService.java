package com.fiveLink.linkOffice.document.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fiveLink.linkOffice.document.domain.DocumentFile;
import com.fiveLink.linkOffice.document.domain.DocumentFileDto;
import com.fiveLink.linkOffice.document.domain.DocumentFolder;
import com.fiveLink.linkOffice.document.repository.DocumentFileRepository;
import com.fiveLink.linkOffice.document.repository.DocumentFolderRepository;

@Service
public class DocumentFileService {

	private final DocumentFileRepository documentFileRepository;
	private final DocumentFolderRepository documentFolderRepository;
	
	private static final Logger LOGGER
	= LoggerFactory.getLogger(DocumentFileService.class);
	
	@Autowired
	public DocumentFileService(DocumentFileRepository documentFileRepository,
			DocumentFolderRepository documentFolderRepository) {
		this.documentFileRepository = documentFileRepository;
		this.documentFolderRepository = documentFolderRepository;
	}
	// 개인 폴더에 파일 가져오는 메소드 
	public List<DocumentFileDto> selectPersonalfileList(Long memberNo, Long folderId){
		// 파일 상태 = 0
		Long fileStatus = 0L;
		List<DocumentFile> documentFileList =
				documentFileRepository.findByMemberMemberNoAndDocumentFolderDocumentFolderNoAndDocumentFileStatus(memberNo, folderId, fileStatus);
		List<DocumentFileDto> documentFileDtoList = new ArrayList<DocumentFileDto>();
		for(DocumentFile d : documentFileList) {
			DocumentFileDto fileDto = d.toDto();
			documentFileDtoList.add(fileDto);
		}		
		
		return documentFileDtoList;
	}
	
	// 개인 폴더 모든 파일 용량 
	public double getAllFileSize(Long memberNo) {
		double formatTotalSize = 0;
		Long folderStatus = 0L;
		Long docBoxType = 1L;
		Long fileStatus = 0L;
		List<DocumentFolder> folderList = 
				documentFolderRepository.findByMemberMemberNoAndDocumentBoxTypeAndDocumentFolderStatus(memberNo, docBoxType, folderStatus);
		
		if(folderList != null && !folderList.isEmpty()) {
			List<Long> folderNoList = folderList.stream()
	                .map(DocumentFolder::getDocumentFolderNo)
	                .collect(Collectors.toList());

	        // 모든 파일 목록 가져오기
	        List<DocumentFile> allFileList = new ArrayList<>();
	        for (Long folderNo : folderNoList) {
	            List<DocumentFile> filesInFolder = 
	            		documentFileRepository.findByDocumentFolderDocumentFolderNoAndDocumentFileStatus(folderNo, fileStatus);
	            if (filesInFolder != null && !filesInFolder.isEmpty()) {
	            	allFileList.addAll(filesInFolder);
	            }
	        }

	        // 모든 파일 사이즈를 합산
	        double totalSize = 0;
	        if (!allFileList.isEmpty()) {
	            for (DocumentFile file : allFileList) {
	                String fileSizeStr = file.getDocumentFileSize();
	                if (fileSizeStr != null && !fileSizeStr.isEmpty()) {
	                    double fileSize = Double.parseDouble(fileSizeStr.replaceAll("[^0-9.]", ""));
	                    totalSize += fileSize;
	                }
	            }
	            // KB에서 GB로 변환
	            double totalSizeGB = totalSize / (1024*1024); 
	            formatTotalSize = Math.ceil(totalSizeGB * 100) / 100.0;
	        }
	    }
		
		return formatTotalSize;
	}
	
	// 휴지통 
	public List<DocumentFileDto> documentBinList(Long member_no){
		// 파일 상태 = 1
		Long document_file_status = 1L;
		// repository에 memberNo, file_Status를 넘겨줌 
		List<DocumentFile> documentFileList = 
				documentFileRepository.findByMemberMemberNoAndDocumentFileStatus(member_no, document_file_status);
		List<DocumentFileDto> documentFileDtoList = new ArrayList<DocumentFileDto>();
		for(DocumentFile d : documentFileList) {
			DocumentFileDto fileDto = d.toDto();
			documentFileDtoList.add(fileDto);
		}		
		return documentFileDtoList;
	}
}
