package com.example.portfolio.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @Column(nullable = false)
    private Boolean isImage; // 이미지 여부

    @Column(length = 255)
    private String name; // 파일 이름

    @Column(nullable = false)
    private Long size; // 파일 크기

    @Column(length = 100)
    private String type; // 파일 타입 (MIME 타입)

    @Column(length = 500)
    private String url; // 파일 경로

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "board_id") // 외래 키
    private Board board;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id") // 외래 키
    private Users user;
}

