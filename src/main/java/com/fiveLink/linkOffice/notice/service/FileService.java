package com.fiveLink.linkOffice.notice.service;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fiveLink.linkOffice.notice.repository.NoticeRepository;

@Service
public class FileService {
	
	private String fileDir = "C:\\notice\\upload\\"; // 저장 경로
	
	private final NoticeRepository NoticeRepository;
	
	@Autowired
	public FileService(NoticeRepository boardRepository) {
		this.NoticeRepository = boardRepository;
	}

	// 파일 업로드 로직
	public String upload(MultipartFile file) {
		String newFileName = null;
		
		try {
			// 1. 파일 원래 이름
			String oriFileName = file.getOriginalFilename();
			// 2. 파일 확장자 추출
			String fileExt = oriFileName.substring(oriFileName.lastIndexOf("."), oriFileName.length());
			// 3. 파일 이름을 UUID로 변경
			UUID uuid = UUID.randomUUID();
			// 4. UUID에서 하이픈 제거
			String uniqueName = uuid.toString().replaceAll("-", "");
			// 5. 새로운 파일명 생성
			newFileName = uniqueName + fileExt;
			
			// 6. 저장할 디렉토리 경로 설정
			File saveDir = new File(fileDir);
			// 7. 디렉토리가 없을 경우 생성
			if (!saveDir.exists()) {
				saveDir.mkdirs();  // 경로 중간에 없는 디렉토리도 생성
			}
			
			// 8. 저장할 파일 객체 생성 (디렉토리 + 새로운 파일명)
			File saveFile = new File(saveDir, newFileName);
			
			// 9. 파일 저장
			file.transferTo(saveFile);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return newFileName;
	}
}
