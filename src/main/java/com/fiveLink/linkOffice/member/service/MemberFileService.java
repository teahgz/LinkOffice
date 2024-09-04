package com.fiveLink.linkOffice.member.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class MemberFileService {
	// 전자결재 이미지 파일경로
    private final String fileDigitalDir = "C:\\linkoffice\\upload\\member\\digital\\";

    public String uploadDigital(String base64Image) {
        String newDigitalName = null;

        try {
            String[] parts = base64Image.split(",");
            String data = parts[1];

            String fileExt = ".png";
            UUID uuid = UUID.randomUUID();
            String uniqueName = uuid.toString().replaceAll("-", "");
            newDigitalName = uniqueName + fileExt;
            File saveFile = new File(fileDigitalDir + newDigitalName);

            File dir = new File(fileDigitalDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            byte[] imageBytes = Base64.getDecoder().decode(data);
            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                fos.write(imageBytes);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return newDigitalName;
    }
}
