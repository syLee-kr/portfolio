package com.example.portfolio.controller;

import com.example.portfolio.domain.Event;
import com.example.portfolio.domain.Users;
import com.example.portfolio.service.event.EventService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    // 모든 이벤트 조회
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    // 특정 유저의 이벤트 조회
    @GetMapping("/user/events")
    public List<Event> getEventsByUser(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("로그인 정보가 없습니다.");
        }

        // 사용자 ID를 기준으로 이벤트 가져오기
        return eventService.getEventsByUser(user.getUserId());
    }

    @PostMapping
    public Event createEvent(HttpSession session, @RequestBody Event event) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("로그인 정보가 없습니다.");
        }
        event.setUser(user);

        // 이벤트 유형별 기본 색상 설정
        if (event.getColor() == null) {
            switch (event.getType()) {
                case "휴가":
                    event.setColor("#007bff"); // 파랑
                    break;
                case "업무":
                    event.setColor("#ffc107"); // 노랑
                    break;
                case "행사":
                    event.setColor("#dc3545"); // 빨강
                    break;
                default:
                    event.setColor("#6c757d"); // 기본 회색
            }
        }

        return eventService.createEvent(user.getUserId(), event);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable Long eventId, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("로그인 정보가 없습니다.");
        }
        eventService.deleteEvent(user.getUserId(), eventId);
    }

}
