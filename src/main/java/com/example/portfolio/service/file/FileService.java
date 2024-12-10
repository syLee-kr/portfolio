package com.example.portfolio.service.file;

import com.example.portfolio.domain.Board;
import com.example.portfolio.domain.File;
import com.example.portfolio.domain.Users;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    String uploadFiles(List<MultipartFile> uploadFiles, Users user, Board board) throws Exception;

    List<File> getAllFiles();

    List<File> getFilesByUser(Users user);

    File getFileById(Long id);
}
