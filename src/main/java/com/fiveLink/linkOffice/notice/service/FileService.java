package com.fiveLink.linkOffice.notice.service;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fiveLink.linkOffice.notice.domain.Notice;
import com.fiveLink.linkOffice.notice.repository.NoticeRepository;

@Service
public class FileService {
	
	private String fileDir = "C:\\linkoffice\\upload\\"; 
	
	private final NoticeRepository NoticeRepository;
	
	@Autowired
	public FileService(NoticeRepository boardRepository) {
		this.NoticeRepository = boardRepository;
	}
	
	public ResponseEntity<Object> download(Long notice_no){
		try {
			Notice n = NoticeRepository.findBynoticeNo(notice_no);
			
			String newFileName = n.getNoticeNewImg();
			String oriFileName = URLEncoder.encode(n.getNoticeOriImg(),"UTF-8");
			String downDir = fileDir+newFileName;
			
			Path filePath = Paths.get(downDir);
			Resource resource = new InputStreamResource(Files.newInputStream(filePath));
			
			File file = new File(downDir);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDisposition(ContentDisposition.builder("attachment").filename(oriFileName).build());
			
			return new ResponseEntity<Object>(resource, headers, HttpStatus.OK);
			
		}catch(Exception e){
			e.printStackTrace();
			return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
		}
	}

	public String upload(MultipartFile file) {
	    String newFileName = null;

	    try {
	        // 파일이 비어 있는지 확인
	        if (file.isEmpty()) {
	            return null; // 파일이 없으면 null 반환
	        }

	        // 1. 파일 원래 이름
	        String oriFileName = file.getOriginalFilename();
	        // 2. 파일 확장자 추출 (확장자가 없는 경우 대비)
	        int dotIndex = oriFileName.lastIndexOf(".");
	        String fileExt = "";
	        if (dotIndex != -1) {
	            fileExt = oriFileName.substring(dotIndex);
	        }
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

	
	public int delete(Long notice_no){
		int result = -1;
		try {
		Notice n = NoticeRepository.findBynoticeNo(notice_no);
		String newFileName = n.getNoticeNewImg(); 
		String oriFileName = n.getNoticeOriImg(); 
		String resultDir = fileDir + URLDecoder.decode(newFileName,"UTF-8");
		
		if(resultDir != null && resultDir.isEmpty() == false) {
			File file = new File(resultDir);
			
			if(file.exists()) {
				file.delete();
				result = 1;
			}
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
