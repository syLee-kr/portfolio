package com.example.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalenderController {

    @GetMapping("/calendar")
    public String showCalendar() {
        return "calendar/calendar"; // calendar.html 파일을 반환
    }
}