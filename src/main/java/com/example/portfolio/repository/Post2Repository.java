package com.example.portfolio.repository;

import com.example.portfolio.domain.Post2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Post2Repository extends JpaRepository<Post2, Integer> {

    // 최근 항목을 COUNT 기준으로 정렬 후 페이징 처리
    Page<Post2> findAllByOrderByCountDesc(Pageable pageable);
}

