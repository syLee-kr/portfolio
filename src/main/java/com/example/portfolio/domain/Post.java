package com.example.portfolio.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pseq; // 게시물 ID

    @Column(length = 255)
    private String title; // 제목

    @Column(length = 20)
    private String views; // 조회수

    @Column(length = 50)
    private String uploadDay; // 업로드 일자

    @Column(length = 20)
    private String anviews; // 평균 일일 조회수

    @Column(length = 20)
    private String allanv; // 전체 평균 일일 조회수

    @Column(length = 500)
    private String URLLink; // 링크

    @Column(name = "post_date")
    private LocalDate postDate; // 게시 날짜

    @PrePersist
    protected void onCreate() {
        this.postDate = LocalDate.now(); // 저장 시 현재 날짜로 설정
    }
}

