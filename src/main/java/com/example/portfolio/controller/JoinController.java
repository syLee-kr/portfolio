package com.example.portfolio.controller;

import com.example.portfolio.domain.Users;
import com.example.portfolio.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/join")
public class JoinController {

    private static final Logger logger = LoggerFactory.getLogger(JoinController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public JoinController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입 페이지 반환
     */
    @GetMapping
    public String showJoinPage(Model model) {
        logger.info("Accessing the join page");
        model.addAttribute("user", new Users()); // 빈 Users 객체 전달
        return "login/join"; // 회원가입 페이지로 이동
    }

    /**
     * 아이디 중복 체크 API
     */
    @GetMapping("/check-duplicate-id")
    @ResponseBody
    public Map<String, Boolean> checkDuplicateId(@RequestParam String userId) {
        logger.debug("Checking duplicate user ID: {}", userId);
        boolean exists = userService.findById(userId) != null;
        logger.debug("User ID {} exists: {}", userId, exists);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }

    /**
     * 회원가입 처리
     */
    @PostMapping("/joinOk")
    public String processJoin(@ModelAttribute Users user, Model model) {
        logger.info("Processing join request for user ID: {}", user.getUserId());
        Users existingUser = userService.findById(user.getUserId());

        if (existingUser != null) {
            logger.warn("Join attempt with existing user ID: {}", user.getUserId());
            model.addAttribute("errorMessage", "이미 사용 중인 아이디입니다.");
            model.addAttribute("user", user);
            return "login/join";
        }

        try {
            logger.debug("Encoding password for user ID: {}", user.getUserId());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("ROLE_USER");
            user.setStatus(Users.Status.PENDING);
            logger.debug("Saving new user: {}", user.getUserId());
            userService.save(user);
            logger.info("User {} registered successfully", user.getUserId());
        } catch (Exception e) {
            logger.error("Error during user registration for ID {}: {}", user.getUserId(), e.getMessage(), e);
            model.addAttribute("errorMessage", "회원가입 처리 중 오류가 발생했습니다.");
            model.addAttribute("user", user);
            return "login/join";
        }

        return "redirect:/login";
    }
}
