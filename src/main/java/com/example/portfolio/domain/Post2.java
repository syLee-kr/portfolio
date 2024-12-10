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
@Table(name = "post2")
public class Post2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pseq; // 기본 키

    @Column(length = 100)
    private String popularWord; // 인기 단어

    @Column(nullable = false)
    private int count; // 횟수

    @Column(length = 255)
    private String relatedWords; // 연관 단어

    @Column(length = 500)
    private String link1; // 링크 1

    @Column(length = 500)
    private String link2; // 링크 2

    @Column(name = "post_date")
    private LocalDate postDate;

    @PrePersist
    protected void onCreate() {
        this.postDate = LocalDate.now();
    }
}

