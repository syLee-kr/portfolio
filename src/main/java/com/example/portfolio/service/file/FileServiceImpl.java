package com.example.portfolio.service.file;

import com.example.portfolio.domain.Board;
import com.example.portfolio.domain.File;
import com.example.portfolio.domain.Users;
import com.example.portfolio.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final long MAX_TOTAL_SIZE = 1L * 1024 * 1024 * 1024; // 1GB
    private final int MAX_FILE_COUNT = 50;
    private final Path storagePath = Paths.get("uploads"); // 파일 저장 경로

    private long currentTotalSize = 0;

    @Autowired
    private FileRepository fileRepository;

    public FileServiceImpl() {
        try {
            Files.createDirectories(storagePath);
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 경로 생성 실패", e);
        }
    }

    @Override
    public String uploadFiles(List<MultipartFile> uploadFiles, Users user, Board board) throws Exception {
        if (uploadFiles.size() > MAX_FILE_COUNT) {
            throw new RuntimeException("파일 개수 초과: 최대 50개의 파일만 업로드할 수 있습니다.");
        }

        long totalSize = uploadFiles.stream().mapToLong(MultipartFile::getSize).sum();
        if (currentTotalSize + totalSize > MAX_TOTAL_SIZE) {
            throw new RuntimeException("총 파일 용량 초과: 최대 1GB만 업로드할 수 있습니다.");
        }

        List<String> savedFileUrls = new ArrayList<>();
        for (MultipartFile uploadFile : uploadFiles) {
            String fileId = UUID.randomUUID().toString();
            String fileName = uploadFile.getOriginalFilename();
            String contentType = uploadFile.getContentType();
            long fileSize = uploadFile.getSize();
            boolean isImage = contentType != null && contentType.startsWith("image");

            // 저장 로직
            Path filePath = storagePath.resolve(fileId + "_" + fileName);
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                fos.write(uploadFile.getBytes());
            }

            // File 엔티티 생성
            File file = File.builder()
                    .name(fileName)
                    .type(contentType)
                    .size(fileSize)
                    .url("/files/" + filePath.getFileName())
                    .isImage(isImage)
                    .user(user)
                    .board(board)
                    .build();

            fileRepository.save(file);
            savedFileUrls.add(file.getUrl());
        }

        currentTotalSize += totalSize;

        return "업로드 성공. 파일 URL 목록: " + savedFileUrls;
    }

    @Override
    public List<File> getAllFiles() {
        return fileRepository.findAll();
    }

    @Override
    public List<File> getFilesByUser(Users user) {
        return fileRepository.findByUser(user);
    }

    @Override
    public File getFileById(Long id) {
        return fileRepository.findById(id).orElse(null);
    }
}
