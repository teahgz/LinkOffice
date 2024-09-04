package com.fiveLink.linkOffice.member.service;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
public class MemberFileService {

    // 전자결재 이미지 파일경로
    private final String fileDigitalDir = "C:\\linkoffice\\upload\\member\\digital\\";

    public String uploadDigital(String base64Image) throws IOException {
        // Base64 문자열에서 실제 이미지 데이터 추출
        String[] parts = base64Image.split(",");
        String data = parts[1];

        // 파일 확장자 및 파일 이름 생성
        String fileExt = ".png";
        UUID uuid = UUID.randomUUID();
        String uniqueName = uuid.toString().replaceAll("-", "");
        String newDigitalName = uniqueName + fileExt;
        File saveFile = new File(fileDigitalDir + newDigitalName);

        // 디렉토리가 존재하지 않으면 생성
        File dir = new File(fileDigitalDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Base64 데이터를 디코딩하여 파일로 저장
        byte[] imageBytes = Base64.getDecoder().decode(data);
        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
            fos.write(imageBytes);
        }

        return newDigitalName;
    }
}
