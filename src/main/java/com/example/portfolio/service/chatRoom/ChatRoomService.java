package com.example.portfolio.service.chatRoom;

import com.example.portfolio.domain.ChatRoom;
import java.util.List;

public interface ChatRoomService {
    List<ChatRoom> findAllRooms();
    ChatRoom findRoomById(Long roomId);
    ChatRoom createRoom(String name);
    // 필요하다면 추후 참가자 추가, 메시지 불러오기 등의 메서드 추가
    // 추가된 메서드
    ChatRoom createRoomForUsers(String userId1, String userId2); // 두 유저 간 채팅방 생성
    List<Long> findRoomsByUserId(String userId); // 특정 유저가 속한 채팅방 검색
    void deleteRoom(Long roomId); // 채팅방 삭제
}
