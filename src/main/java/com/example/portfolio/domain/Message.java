package com.example.portfolio.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 발신자 정보
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    @JsonBackReference
    private Users user;

    // 메시지가 속한 채팅방
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false, length = 1000)
    private String content;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date sendTime = new Date();

    @Column(length = 50)
    private String messageType = "TEXT";

    @PrePersist
    public void prePersist() {
        this.sendTime = new Date();
    }
}
