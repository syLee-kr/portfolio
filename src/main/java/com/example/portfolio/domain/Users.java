package com.example.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class Users {

    @Id
    @Column(name = "user_id", columnDefinition = "VARCHAR(255)")
    private String userId;

    @Column(nullable = false, length = 255)
    private String password; // 비밀번호

    @Column(length = 20)
    private String birthday; // 생일

    @Column(length = 100)
    private String name; // 이름

    @Column(length = 20)
    private String phone; // 전화번호

    @Column(length = 255)
    private String address; // 주소

    @Column(length = 255)
    private String email; // 이메일

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date regdate = new Date(); // 가입일

    @Column(columnDefinition = "varchar(255) default 'images/default.png'")
    private String profileImage; // 프로필 이미지

    @Column(nullable = false, columnDefinition = "varchar(255) default 'ROLE_USER'")
    private String role; // 권한 (예: ROLE_USER, ROLE_ADMIN)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING; // 기본값은 Java 코드에서 설정

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Event> events;

    // 유저가 참여하는 여러 채팅방
    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private Set<ChatRoom> chatRooms;

    public enum Status {
        PENDING, // 대기 상태
        USER,    // 일반 유저
        ADMIN    // 관리자
    }

    @PrePersist
    public void prePersist() {
        this.role = this.role == null ? "ROLE_USER" : this.role;
        this.status = this.status == null ? Status.PENDING : this.status;
        this.regdate = new Date();
    }
}
