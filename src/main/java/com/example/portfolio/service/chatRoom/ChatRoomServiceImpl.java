package com.example.portfolio.service.chatRoom;

import com.example.portfolio.domain.ChatRoom;
import com.example.portfolio.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomServiceImpl(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    @Override
    public List<ChatRoom> findAllRooms() {
        return chatRoomRepository.findAll();
    }

    @Override
    public ChatRoom findRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElse(null);
    }

    @Override
    public ChatRoom createRoom(String name) {
        ChatRoom room = ChatRoom.builder()
                .name(name)
                .build();
        return chatRoomRepository.save(room);
    }
    @Override
    public ChatRoom createRoomForUsers(String userId1, String userId2) {
        // 두 유저 간 채팅방이 이미 존재하는지 확인
        Optional<ChatRoom> existingRoomOpt = chatRoomRepository.findRoomByParticipants(userId1, userId2);

        if (existingRoomOpt.isPresent()) {
            return existingRoomOpt.get(); // 기존 채팅방 반환
        }

        // 새 채팅방 생성
        ChatRoom room = ChatRoom.builder()
                .name("Private Chat")
                .build();

        // 유저를 채팅방에 추가 (DB에서 관련 엔티티 연결 필요)
        chatRoomRepository.save(room); // 채팅방 저장
        return room;
    }


    @Override
    public List<Long> findRoomsByUserId(String userId) {
        return chatRoomRepository.findRoomIdsByUserId(userId);
    }

    @Override
    public void deleteRoom(Long roomId) {
        if (chatRoomRepository.existsById(roomId)) {
            chatRoomRepository.deleteById(roomId);
        }
    }
}
