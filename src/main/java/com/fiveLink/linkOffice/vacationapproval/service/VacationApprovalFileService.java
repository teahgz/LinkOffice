package com.fiveLink.linkOffice.vacationapproval.service;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fiveLink.linkOffice.vacationapproval.repository.VacationApprovalRepository;

@Service
public class VacationApprovalFileService {
	
	private final VacationApprovalRepository vacationApprovalRepository;
	
	@Autowired
	public VacationApprovalFileService(VacationApprovalRepository vacationApprovalRepository) {
		this.vacationApprovalRepository = vacationApprovalRepository;
	}
	
	private final String vacationFileDir = "C:\\linkoffice\\upload\\approval\\vacation\\";
	
    public String uploadVacation(MultipartFile file) {
    	String newVacationFile = null;
    	
    	try {
    		String oriVacationFile = file.getOriginalFilename();
    		String fileExt = oriVacationFile.substring(oriVacationFile.lastIndexOf("."),oriVacationFile.length());
    		UUID uuid = UUID.randomUUID();
    		String uniqueName = uuid.toString().replaceAll("-", "");
    		newVacationFile = uniqueName+fileExt;
    		File saveFile = new File(vacationFileDir+newVacationFile);
    		if(!saveFile.exists()) {
    			saveFile.mkdirs();
    		}
    		file.transferTo(saveFile);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return newVacationFile;
    }
}
