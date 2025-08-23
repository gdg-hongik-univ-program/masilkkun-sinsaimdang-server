package com.sinsaimdang.masilkkoon.masil.common.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Profile("prod") // prod 프로필에서만 활동
public class S3Uploader implements Uploader {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return upload(uploadFile, dirName);
    }

    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile);  // 로컬에 생성된 임시 파일 삭제

        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("임시 파일이 삭제되었습니다.");
        } else {
            log.info("임시 파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    @Override
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty() || fileUrl.contains("default_profile.png")) {
            log.info("삭제할 파일 URL이 없거나 기본 이미지입니다.");
            return;
        }
        try {
            // 1. S3 버킷 URL을 기반으로 파일 키(경로+이름)를 추출합니다.
            // 예: https://버킷이름.s3.ap-northeast-2.amazonaws.com/profile-images/uuid_파일명.jpg
            // -> profile-images/uuid_파일명.jpg
            final String key = fileUrl.substring(fileUrl.indexOf(".com/") + 5);

            // 2. S3 클라이언트를 사용하여 객체 삭제를 요청합니다.
            amazonS3Client.deleteObject(bucket, key);
            log.info("S3 파일 삭제 완료: {}", fileUrl);

        } catch (Exception e) {
            log.error("S3 파일 삭제 중 오류 발생. URL: {}", fileUrl, e);
        }
    }
}