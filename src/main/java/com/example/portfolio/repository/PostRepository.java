package com.example.portfolio.repository;

import com.example.portfolio.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("SELECT p FROM Post p ORDER BY p.pseq DESC")
    List<Post> findAllByOrderByPseqDesc();
}

