package com.example.portfolio.controller;

import com.example.portfolio.UserDTO;
import com.example.portfolio.domain.ChatRoom;
import com.example.portfolio.domain.Users;
import com.example.portfolio.service.chatRoom.ChatRoomService;
import com.example.portfolio.service.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final UserService userService;

    @Autowired
    public ChatController(ChatRoomService chatRoomService, UserService userService) {
        this.chatRoomService = chatRoomService;
        this.userService = userService;
    }

    /**
     * 채팅 메인 페이지: 생성된 채팅방 목록 등을 출력할 수 있는 페이지로 가정.
     * 예: /chat 접근 시, 현재 사용 가능한 채팅방 목록을 보여주는 페이지.
     */
    @GetMapping
    public String chatMain(HttpSession session, Model model) throws JsonProcessingException {
        if (session == null || session.getAttribute("user") == null) {
            return "login/login";
        }

        Users currentUser = (Users) session.getAttribute("user");
        UserDTO currentUserDTO = new UserDTO(currentUser);
        model.addAttribute("user", currentUserDTO);

        // Users 엔티티를 UserDTO로 변환
        List<UserDTO> userList = userService.findAllUsers()
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String userListJson = objectMapper.writeValueAsString(userList); // JSON으로 변환
        model.addAttribute("userListJson", userListJson);
        System.out.println("userListJson = " + userListJson);

        return "chat/chat-main";
    }


    /**
     * 특정 채팅방 접속 페이지
     * 예: /chat/room/1 접근 시 roomId=1인 채팅방에 입장하는 페이지를 띄운다.
     * 실제 메시지나 유저 정보는 추가 AJAX 요청으로 가져오는 형태를 가정.
     */
    @GetMapping("/room/{roomId}")
    public String enterChatRoom(@PathVariable Long roomId, Model model) {
        ChatRoom room = chatRoomService.findRoomById(roomId);
        if (room == null) {
            // 채팅방 존재하지 않을 경우 에러 처리 로직
            return "error/404";
        }
        model.addAttribute("room", room);
        return "chat"; // 동일한 chat.html 템플릿을 사용하고, JS 측에서 room 정보에 따라 처리
    }

    /**
     * 채팅방 생성 요청 처리 (form submit 이나 AJAX 요청을 통해 새 채팅방 생성)
     */
    @PostMapping("/room/createByName")
    public String createRoomByName(@RequestParam String name) {
        chatRoomService.createRoom(name);
        return "redirect:/chat";
    }

    /**
     * 채팅방 생성 요청 처리 (두 유저 간 채팅방 생성)
     */
    @PostMapping("/room/createForUsers")
    @ResponseBody
    public ChatRoom createRoomForUsers(@RequestParam String userId, HttpSession session) {
        // 세션에서 현재 유저 가져오기
        Users currentUser = (Users) session.getAttribute("user");
        if (currentUser == null) {
            throw new IllegalStateException("User is not logged in");
        }

        // 현재 유저 ID를 가져와 서비스에 전달
        String currentUserId = currentUser.getUserId(); // Users 엔티티에 getUserId() 메서드가 있다고 가정
        return chatRoomService.createRoomForUsers(currentUserId, userId);
    }

}
