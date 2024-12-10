package com.example.portfolio.repository;

import com.example.portfolio.domain.File;
import com.example.portfolio.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByUser(Users user);
}
