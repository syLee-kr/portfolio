package com.example.portfolio.repository;

import com.example.portfolio.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // ID 기준으로 정렬
    List<Message> findByChatRoomIdOrderByIdAsc(Long chatRoomId);

    // 특정 채팅방에 속한 메시지 목록 조회
    List<Message> findByChatRoomIdOrderBySendTimeAsc(Long chatRoomId);

    // 특정 유저가 보낸 메시지 조회
    List<Message> findByUserUserId(String userId);

    // 특정 메시지 타입으로 필터링된 메시지 조회
    List<Message> findByMessageType(String messageType);
}
