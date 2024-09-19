package com.fiveLink.linkOffice.vacationapproval.service;

import java.io.File;
import java.net.URLDecoder;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fiveLink.linkOffice.vacationapproval.domain.VacationApproval;
import com.fiveLink.linkOffice.vacationapproval.repository.VacationApprovalFileRepository;
import com.fiveLink.linkOffice.vacationapproval.repository.VacationApprovalRepository;

@Service
public class VacationApprovalFileService {
	
	private final VacationApprovalRepository vacationApprovalRepository;
	private final VacationApprovalFileRepository vacationApprovalFileRepository;
	
	@Autowired
	public VacationApprovalFileService(VacationApprovalRepository vacationApprovalRepository, VacationApprovalFileRepository vacationApprovalFileRepository) {
		this.vacationApprovalRepository = vacationApprovalRepository;
		this.vacationApprovalFileRepository = vacationApprovalFileRepository;
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
    
    public int delete(Long vapNo) {
    	int result = -1;
    	
    	try {
    		VacationApproval dto = vacationApprovalRepository.findByVacationApprovalNo(vapNo);
    		
    		String newFile = dto.getVacationApprovalFile().getVacationApprovalFileNewName();
    		
    		String resultDir = vacationFileDir + URLDecoder.decode(newFile,"UTF-8");
    		
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
    
    public boolean existsFileForVacationApproval(Long vacationApprovalNo) {
        VacationApproval vacationApproval = vacationApprovalRepository.findByVacationApprovalNo(vacationApprovalNo);
        if (vacationApproval == null) {
            return false;
        }
        return vacationApprovalFileRepository.existsByVacationApproval(vacationApproval);
    }
}
