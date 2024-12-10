package com.example.portfolio.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class GoogleCloudStorageService {

    private final Storage storage;
    private final String bucketName;

    public GoogleCloudStorageService(@Value("${gcp.bucket.name}") String bucketName) throws IOException {
        this.bucketName = bucketName;

        // 환경 변수를 활용하거나 기본 Application Default Credentials 사용
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    /**
     * 파일을 Google Cloud Storage에 업로드
     *
     * @param file 업로드할 MultipartFile
     * @return 업로드된 파일의 URL
     * @throws IOException 파일 처리 중 발생할 수 있는 예외
     */
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        String fileName = generateUniqueFileName(file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream()) {
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, inputStream);
            return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
        } catch (Exception e) {
            throw new IOException("Google Cloud Storage에 파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 유니크한 파일명을 생성
     *
     * @param originalFileName 원본 파일명
     * @return 유니크 파일명
     */
    private String generateUniqueFileName(String originalFileName) {
        String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.]", "_");
        String fileExtension = "";

        if (sanitizedFileName.contains(".")) {
            int extensionIndex = sanitizedFileName.lastIndexOf(".");
            fileExtension = sanitizedFileName.substring(extensionIndex);
            sanitizedFileName = sanitizedFileName.substring(0, extensionIndex);
        }

        return System.currentTimeMillis() + "_" + sanitizedFileName + fileExtension;
    }
}
