package com.sinsaimdang.masilkkoon.masil.common.s3;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface Uploader {
    String upload(MultipartFile multipartFile, String dirName) throws IOException;
    void delete(String fileUrl); // 파일 URL을 받아 파일을 삭제하는 메서드
}