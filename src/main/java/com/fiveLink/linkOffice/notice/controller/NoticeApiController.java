package com.fiveLink.linkOffice.notice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fiveLink.linkOffice.notice.domain.NoticeDto;
import com.fiveLink.linkOffice.notice.service.FileService;
import com.fiveLink.linkOffice.notice.service.NoticeService;


@Controller
public class NoticeApiController {
	private final NoticeService noticeService;
	private final FileService fileService;


	@Autowired
    public NoticeApiController(NoticeService noticeService, FileService fileService) {
        this.noticeService = noticeService;
        this.fileService = fileService;
    }
	
	@ResponseBody
	@PostMapping("/notice/create")
	public Map<String, String> createNotice(NoticeDto dto, 
	                                        @RequestParam("manager") String managerName, 
	                                        @RequestParam("file") MultipartFile file) {
	    Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "404");
	    resultMap.put("res_msg", "공지사항 등록 중 오류가 발생했습니다.");

	    try {
	        // 중요 공지사항 개수 확인
	        int importantNoticeCount = noticeService.countImportantNotices();
	        if (dto.getNotice_importance() == 1 && importantNoticeCount >= 3) {
	            resultMap.put("res_msg", "중요 공지사항은 최대 3개까지 등록 가능합니다.");
	            return resultMap;  // 중요 공지사항이 3개 이상일 때 중단
	        }

	        // 매니저 이름을 사용하여 매니저 번호 조회
	        Long managerNo = noticeService.findMemberNoByManagerName(managerName);
	        dto.setMember_no(managerNo);

	        // 파일이 비어 있는지 확인
	        if (!file.isEmpty()) {
	            // 파일 업로드 처리
	            String savedFileName = fileService.upload(file);
	            if (savedFileName != null) {
	                dto.setNotice_ori_img(file.getOriginalFilename());
	                dto.setNotice_new_img(savedFileName);
	            } else {
	                resultMap.put("res_msg", "파일 업로드가 실패하였습니다.");
	                return resultMap; // 파일 업로드 실패 시 공지사항 등록 중단
	            }
	        }

	        // 공지사항 등록 처리
	        if (noticeService.createNotice(dto) != null) {
	            resultMap.put("res_code", "200");
	            resultMap.put("res_msg", "공지사항이 성공적으로 등록되었습니다.");
	        }
	    } catch (Exception e) {
	        resultMap.put("res_msg", "공지사항 등록 중 오류가 발생했습니다.");
	    }

	    return resultMap;
	}
	
	@GetMapping("/download/{notice_no}")
	public ResponseEntity<Object> noticeImgDownload(
			@PathVariable("notice_no")Long notice_no){
				return fileService.download(notice_no);
			}
	
	@ResponseBody
	@PostMapping("/notice/update/{notice_no}")
	public Map<String,String> updateNotice(NoticeDto dto,
			@RequestParam(name="file",required=false)MultipartFile file){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "게시글 수정중 오류가 발생했습니다.");
		
		if(file != null && "".equals(file.getOriginalFilename()) == false) {
			String savedFileName = fileService.upload(file);
			if(savedFileName != null) {
				dto.setNotice_ori_img(savedFileName);
				dto.setNotice_new_img(savedFileName);
				
				if(fileService.delete(dto.getNotice_no())>0){
					resultMap.put("res_msg", "기존 파일이 정상적으로 삭제 되었습니다.");
				}
				}else {
					resultMap.put("res_msg", "파일 업로드가 실패하였습니다.");
				}				
			}
		
		if(noticeService.updateNotice(dto) != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "게시글이 성공적으로 수정되었습니다.");
		}
		
		return resultMap;
	}
	
	@ResponseBody
	@DeleteMapping("/notice/{notice_no}")
	public Map<String,String> deleteBoard(@PathVariable("notice_no")Long notice_no){
		Map<String,String> map = new HashMap<String,String>();
		map.put("res_code", "404");
		map.put("res_msg","게시글 삭제 중 오류가 발생했습니다.");
		if(fileService.delete(notice_no)>0){
			map.put("res_msg", "기존 파일이 정상적으로 삭제 되었습니다.");
			if(noticeService.deleteNotice(notice_no)>0) {
				map.put("res_code", "200");
				map.put("res_msg","정상적으로 게시글이 삭제되었습니다.");
			}
		}
		return map;
	}
}
