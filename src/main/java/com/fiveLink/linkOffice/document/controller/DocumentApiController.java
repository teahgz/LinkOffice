package com.fiveLink.linkOffice.document.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fiveLink.linkOffice.document.domain.DocumentFolder;
import com.fiveLink.linkOffice.document.repository.DocumentFolderRepository;
import com.fiveLink.linkOffice.document.service.DocumentFileService;
import com.fiveLink.linkOffice.document.service.DocumentFolderService;
import com.fiveLink.linkOffice.member.domain.Member;
import com.fiveLink.linkOffice.member.repository.MemberRepository;
import com.fiveLink.linkOffice.member.service.MemberService;
import com.fiveLink.linkOffice.organization.domain.Department;
import com.fiveLink.linkOffice.organization.repository.DepartmentRepository;

@Controller
public class DocumentApiController {
	
	private final DocumentFolderService documentFolderService;
	private final MemberService memberService;
	private final DocumentFileService documentFileService;
	private MemberRepository memberRepository;
	private DepartmentRepository departmentRepository;
	private DocumentFolderRepository documentFolderRepository;
	
	private static final Logger LOGGER
		= LoggerFactory.getLogger(DocumentApiController.class);
	
	@Autowired
	public DocumentApiController(DocumentFolderService documentFolderService,
			MemberService memberService,
			DocumentFileService documentFileService,
			MemberRepository memberRepository, 
			DepartmentRepository departmentRepository,
			DocumentFolderRepository documentFolderRepository) {
		this.documentFolderService = documentFolderService;
		this.memberService = memberService;
		this.documentFileService = documentFileService;
		this.memberRepository = memberRepository;
		this.departmentRepository = departmentRepository;
		this.documentFolderRepository = documentFolderRepository;
	}
	
   // 개인 첫 폴더 
   @PostMapping("/personal/first/folder")
   @ResponseBody
   public Map<String, String> personalFirstFolder(@RequestBody Map<String, Object> payload){
	  Map<String, String> resultMap = new HashMap<>();
	  resultMap.put("res_code", "404");
	  resultMap.put("res_msg", "경로 오류");
	
	  String folderName = (String) payload.get("folderName");
	  String memberNoStr = (String) payload.get("memberNo");
	  String deptNoStr = (String) payload.get("deptNo");
    
	  Long memberNo = Long.valueOf(memberNoStr);
	  Long deptNo = Long.valueOf(deptNoStr);
      Long folderLevel = 1L;
      Long docBoxType = 0L;
      Long folderStatus = 0L;
      
      Member member = memberRepository.findByMemberNo(memberNo);
      Department department = departmentRepository.findByDepartmentNo(deptNo);

      DocumentFolder documentFolder = DocumentFolder.builder()
            .documentFolderName(folderName)
            .documentFolderLevel(folderLevel)
            .department(department)
            .documentBoxType(docBoxType)
            .member(member)
            .documentFolderStatus(folderStatus)
            .build();

      int result = documentFolderService.personalFirstFolder(documentFolder);

      if (result > 0) {
    	  resultMap.put("res_code", "200");
    	  resultMap.put("res_msg", "폴더 생성이 완료되었습니다.");
      } 
      return resultMap;
   }	
   // 개인 폴더 생성  
   @PostMapping("/personal/create/folder")
   @ResponseBody
   public Map<String, Object> personalCreateFolder(@RequestBody Map<String, Object> payload){
	  Map<String, Object> resultMap = new HashMap<>();
	  resultMap.put("res_code", "404");
	  resultMap.put("res_msg", "경로 오류");
	
	  String folderName = (String) payload.get("folderName");
	  String folderNoStr = (String) payload.get("parentFolderNo");  
	  String memberNoStr = (String) payload.get("memberNo");
	  String deptNoStr = (String) payload.get("deptNo");
    
	  Long memberNo = Long.valueOf(memberNoStr);
	  Long deptNo = Long.valueOf(deptNoStr);
	  Long folderNo = Long.valueOf(folderNoStr);
      Long docBoxType = 0L;
      Long folderStatus = 0L;
      
      Member member = memberRepository.findByMemberNo(memberNo);
      Department department = departmentRepository.findByDepartmentNo(deptNo);
      DocumentFolder documentFolder = documentFolderRepository.findByDocumentFolderNo(folderNo);
      
      Long folderLevel = documentFolder.getDocumentFolderLevel() + 1;
      
      DocumentFolder newDocumentFolder = DocumentFolder.builder()
            .documentFolderName(folderName)
            .documentFolderParentNo(folderNo)            
            .documentFolderLevel(folderLevel)
            .department(department)
            .documentBoxType(docBoxType)
            .member(member)
            .documentFolderStatus(folderStatus)
            .build();

      int result = documentFolderService.personalCreateFolder(newDocumentFolder);

      if (result > 0) {
    	  DocumentFolder createdFolder = documentFolderRepository.findByDocumentFolderNameAndDocumentFolderParentNo(folderName, folderNo);
    	  resultMap.put("res_code", "200");
    	  resultMap.put("res_msg", "폴더 생성이 완료되었습니다.");
    	  resultMap.put("folderNo" , createdFolder.getDocumentFolderNo());
      } 
      return resultMap;
   }
   
   // 부서 첫 폴더 
   @PostMapping("/department/first/folder")
   @ResponseBody
   public Map<String, String> departmentFirstFolder(@RequestBody Map<String, Object> payload){
	  Map<String, String> resultMap = new HashMap<>();
	  resultMap.put("res_code", "404");
	  resultMap.put("res_msg", "경로 오류");
	
	  String folderName = (String) payload.get("folderName");
	  String memberNoStr = (String) payload.get("memberNo");
	  String deptNoStr = (String) payload.get("deptNo");
    
	  Long memberNo = Long.valueOf(memberNoStr);
	  Long deptNo = Long.valueOf(deptNoStr);
      Long folderLevel = 1L;
      Long docBoxType = 1L;
      Long folderStatus = 0L;
      
      Member member = memberRepository.findByMemberNo(memberNo);
      Department department = departmentRepository.findByDepartmentNo(deptNo);

      DocumentFolder documentFolder = DocumentFolder.builder()
            .documentFolderName(folderName)
            .documentFolderLevel(folderLevel)
            .department(department)
            .documentBoxType(docBoxType)
            .member(member)
            .documentFolderStatus(folderStatus)
            .build();

      int result = documentFolderService.departmentFirstFolder(documentFolder);
      System.out.println("Service result: " + result);

      if (result > 0) {
    	  resultMap.put("res_code", "200");
    	  resultMap.put("res_msg", "폴더 생성이 완료되었습니다.");
      } 
      return resultMap;
   }	
 
   // 부서 폴더 생성  
   @PostMapping("/department/create/folder")
   @ResponseBody
   public Map<String, Object> departmentCreateFolder(@RequestBody Map<String, Object> payload){
	  Map<String, Object> resultMap = new HashMap<>();
	  resultMap.put("res_code", "404");
	  resultMap.put("res_msg", "경로 오류");
	
	  String folderName = (String) payload.get("folderName");
	  String folderNoStr = (String) payload.get("parentFolderNo");  
	  String memberNoStr = (String) payload.get("memberNo");
	  String deptNoStr = (String) payload.get("deptNo");
    
	  Long memberNo = Long.valueOf(memberNoStr);
	  Long deptNo = Long.valueOf(deptNoStr);
	  Long folderNo = Long.valueOf(folderNoStr);
      Long docBoxType = 1L;
      Long folderStatus = 0L;
      
      Member member = memberRepository.findByMemberNo(memberNo);
      Department department = departmentRepository.findByDepartmentNo(deptNo);
      DocumentFolder documentFolder = documentFolderRepository.findByDocumentFolderNo(folderNo);
      
      Long folderLevel = documentFolder.getDocumentFolderLevel() + 1;
      
      DocumentFolder newDocumentFolder = DocumentFolder.builder()
            .documentFolderName(folderName)
            .documentFolderParentNo(folderNo)            
            .documentFolderLevel(folderLevel)
            .department(department)
            .documentBoxType(docBoxType)
            .member(member)
            .documentFolderStatus(folderStatus)
            .build();

      int result = documentFolderService.departmentCreateFolder(newDocumentFolder);

      if (result > 0) {
    	  DocumentFolder createdFolder = documentFolderRepository.findByDocumentFolderNameAndDocumentFolderParentNo(folderName, folderNo);
    	  resultMap.put("res_code", "200");
    	  resultMap.put("res_msg", "폴더 생성이 완료되었습니다.");
    	  resultMap.put("folderNo" , createdFolder.getDocumentFolderNo());
      } 
      return resultMap;
   }
   
   // 사내 첫 폴더 
   @PostMapping("/company/first/folder")
   @ResponseBody
   public Map<String, String> companyFirstFolder(@RequestBody Map<String, Object> payload){
	  Map<String, String> resultMap = new HashMap<>();
	  resultMap.put("res_code", "404");
	  resultMap.put("res_msg", "경로 오류");
	
	  String folderName = (String) payload.get("folderName");
	  String memberNoStr = (String) payload.get("memberNo");
	  String deptNoStr = (String) payload.get("deptNo");
    
	  Long memberNo = Long.valueOf(memberNoStr);
	  Long deptNo = Long.valueOf(deptNoStr);
      Long folderLevel = 1L;
      Long docBoxType = 2L;
      Long folderStatus = 0L;
      
      Member member = memberRepository.findByMemberNo(memberNo);
      Department department = departmentRepository.findByDepartmentNo(deptNo);

      DocumentFolder documentFolder = DocumentFolder.builder()
            .documentFolderName(folderName)
            .documentFolderLevel(folderLevel)
            .department(department)
            .documentBoxType(docBoxType)
            .member(member)
            .documentFolderStatus(folderStatus)
            .build();

      int result = documentFolderService.departmentFirstFolder(documentFolder);
      System.out.println("Service result: " + result);

      if (result > 0) {
    	  resultMap.put("res_code", "200");
    	  resultMap.put("res_msg", "폴더 생성이 완료되었습니다.");
      } 
      return resultMap;
   }
   
   // 사내 폴더 생성  
   @PostMapping("/company/create/folder")
   @ResponseBody
   public Map<String, Object> companyCreateFolder(@RequestBody Map<String, Object> payload){
	  Map<String, Object> resultMap = new HashMap<>();
	  resultMap.put("res_code", "404");
	  resultMap.put("res_msg", "경로 오류");
	
	  String folderName = (String) payload.get("folderName");
	  String folderNoStr = (String) payload.get("parentFolderNo");  
	  String memberNoStr = (String) payload.get("memberNo");
	  String deptNoStr = (String) payload.get("deptNo");
    
	  Long memberNo = Long.valueOf(memberNoStr);
	  Long deptNo = Long.valueOf(deptNoStr);
	  Long folderNo = Long.valueOf(folderNoStr);
      Long docBoxType = 2L;
      Long folderStatus = 0L;
      
      Member member = memberRepository.findByMemberNo(memberNo);
      Department department = departmentRepository.findByDepartmentNo(deptNo);
      DocumentFolder documentFolder = documentFolderRepository.findByDocumentFolderNo(folderNo);
      
      Long folderLevel = documentFolder.getDocumentFolderLevel() + 1;
      
      DocumentFolder newDocumentFolder = DocumentFolder.builder()
            .documentFolderName(folderName)
            .documentFolderParentNo(folderNo)            
            .documentFolderLevel(folderLevel)
            .department(department)
            .documentBoxType(docBoxType)
            .member(member)
            .documentFolderStatus(folderStatus)
            .build();

      int result = documentFolderService.companyCreateFolder(newDocumentFolder);

      if (result > 0) {
    	  DocumentFolder createdFolder = documentFolderRepository.findByDocumentFolderNameAndDocumentFolderParentNo(folderName, folderNo);
    	  resultMap.put("res_code", "200");
    	  resultMap.put("res_msg", "폴더 생성이 완료되었습니다.");
    	  resultMap.put("folderNo" , createdFolder.getDocumentFolderNo());
      } 
      return resultMap;
   }
   
   // 폴더 이름 변경
   @PostMapping("/change/folder/name")
   @ResponseBody
   public Map<String, String> changedFolderName(@RequestBody Map<String, Object> payload){
		  Map<String, String> resultMap = new HashMap<>();
		  resultMap.put("res_code", "404");
		  resultMap.put("res_msg", "경로 오류");
		
		  String newFolderName = (String) payload.get("folderName");
		  String folderNoStr = (String) payload.get("folderNo");
	    
		  Long folderNo = Long.valueOf(folderNoStr);
		  
		  DocumentFolder oridocumentfolder = documentFolderRepository.findByDocumentFolderNo(folderNo);

	      DocumentFolder newDocumentFolder = DocumentFolder.builder()
	    		.documentFolderNo(oridocumentfolder.getDocumentFolderNo())
	            .documentFolderName(newFolderName)
	            .documentFolderParentNo(oridocumentfolder.getDocumentFolderParentNo())
	            .documentFolderLevel(oridocumentfolder.getDocumentFolderLevel())
	            .department(oridocumentfolder.getDepartment())
	            .documentBoxType(oridocumentfolder.getDocumentBoxType())
	            .member(oridocumentfolder.getMember())
	            .documentFolderCreateDate(oridocumentfolder.getDocumentFolderCreateDate())
	            .documentFolderUpdateDate(LocalDateTime.now())
	            .documentFolderStatus(oridocumentfolder.getDocumentFolderStatus())
	            .build();
	      if(documentFolderService.changeFolderName(newDocumentFolder) > 0) {
          	resultMap.put("res_code", "200");
          	resultMap.put("res_msg", "폴더명이 변경되었습니다.");  
	      }
	      return resultMap;
	   }
}
