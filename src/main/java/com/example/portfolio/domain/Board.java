package com.example.portfolio.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                      // 게시글 ID

    private String title;                 // 제목

    @Column(columnDefinition = "TEXT")
    private String content;               // 내용

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false)
    private Date regdate = new Date(); // 기본값 설정

    @ManyToOne(fetch = FetchType.EAGER) // Lazy -> Eager로 변경
    @JoinColumn(name = "user_id")
    private Users user;         // 작성자

    @Column(nullable = false, columnDefinition = "int default 0")
    private int views;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files;             // 첨부 파일 목록

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int priority;                 // 중요 게시글 여부 (0: 일반, 1: 중요)
}
