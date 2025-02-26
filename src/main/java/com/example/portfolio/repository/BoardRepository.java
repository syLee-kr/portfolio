package com.example.portfolio.repository;

import com.example.portfolio.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b ORDER BY b.priority DESC, b.regdate DESC")
    Page<Board> findAllOrderByPriority(Pageable pageable);
}
