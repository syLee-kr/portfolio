package com.example.portfolio.repository;

import com.example.portfolio.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 채팅방 이름으로 조회
    Optional<ChatRoom> findByName(String name);

    // 특정 유저가 포함된 채팅방 조회
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.participants p WHERE p.id = :userId")
    Set<ChatRoom> findByParticipantsUserId(String userId);

    // 두 유저 간의 채팅방 찾기
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.participants p1 JOIN cr.participants p2 " +
            "WHERE p1.id = :userId1 AND p2.id = :userId2")
    Optional<ChatRoom> findRoomByParticipants(String userId1, String userId2);

    // 특정 유저가 포함된 채팅방의 ID 조회
    @Query("SELECT cr.id FROM ChatRoom cr JOIN cr.participants p WHERE p.id = :userId")
    List<Long> findRoomIdsByUserId(String userId);
}
