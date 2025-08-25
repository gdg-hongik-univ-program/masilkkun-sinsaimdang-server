package com.sinsaimdang.masilkkoon.masil.common.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@Profile("local") // local 프로필에서만 활동
public class LocalUploader implements Uploader {

    private final String uploadDir = System.getProperty("user.home") + "/masil-uploads/";

    @Override
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        File directory = new File(uploadDir + dirName);
        if (!directory.exists()) {
            directory.mkdirs(); // 폴더가 없으면 생성
        }

        String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        File dest = new File(directory, fileName);
        multipartFile.transferTo(dest);

        String localUrl = "/uploads/" + dirName + "/" + fileName; // 웹 서버 설정에 따라 달라질 수 있는 가상 URL
        log.info("로컬에 파일 업로드 완료: {}", dest.getAbsolutePath());

        // DB에는 로컬 파일 시스템의 절대 경로를 저장합니다.
        return dest.getAbsolutePath().replace("\\", "/");
    }

    @Override
    public void delete(String filePath) { // 파라미터명을 fileUrl -> filePath로 변경하여 명확화
        if (filePath == null || filePath.isEmpty()) {
            log.info("삭제할 파일 경로가 없습니다.");
            return;
        }
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (file.delete()) {
                    log.info("로컬 파일 삭제 완료: {}", filePath);
                } else {
                    log.warn("로컬 파일 삭제 실패: {}", filePath);
                }
            } else {
                log.warn("삭제할 로컬 파일이 존재하지 않습니다: {}", filePath);
            }
        } catch (Exception e) {
            log.error("로컬 파일 삭제 중 오류 발생. 경로: {}", filePath, e);
        }
    }
}