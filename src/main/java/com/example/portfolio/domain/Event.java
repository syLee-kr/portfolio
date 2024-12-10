package com.example.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate; // 날짜만 필요하다면 LocalDate 사용

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 이벤트 ID

    @Column(nullable = false, length = 255)
    private String title; // 이벤트 제목

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // 시작 날짜

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate; // 종료 날짜

    @Column(length = 50, nullable = false)
    private String type; // 이벤트 유형 (휴가, 업무, 행사 등)

    @Column(length = 20, nullable = true)
    private String color; // 이벤트 색상

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false) // 외래키 명시 및 null 허용 불가 설정
    @JsonBackReference // 순환 참조 방지
    private Users user; // 사용자
}
