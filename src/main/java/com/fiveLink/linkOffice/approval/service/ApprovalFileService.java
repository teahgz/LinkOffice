package com.fiveLink.linkOffice.approval.service;

import java.io.File;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ApprovalFileService {

	
	private final String fileDir = "C:\\linkoffice\\upload\\approval\\approval\\";
	
    public String upload(MultipartFile file) {
    	String newFile = null;
    	
    	try {
    		String oriFile = file.getOriginalFilename();
    		String fileExt = oriFile.substring(oriFile.lastIndexOf("."),oriFile.length());
    		UUID uuid = UUID.randomUUID();
    		String uniqueName = uuid.toString().replaceAll("-", "");
    		newFile = uniqueName+fileExt;
    		File saveFile = new File(fileDir+newFile);
    		if(!saveFile.exists()) {
    			saveFile.mkdirs();
    		}
    		file.transferTo(saveFile);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return newFile;
    }
}
