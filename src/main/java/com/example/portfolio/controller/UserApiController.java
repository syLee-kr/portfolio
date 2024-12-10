package com.example.portfolio.controller;

import com.example.portfolio.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserApiController {

    private static final Logger logger = LoggerFactory.getLogger(UserApiController.class);

    private final UserService userService;

    @Autowired
    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/join/check-duplicate-id")
    @ResponseBody
    public Map<String, Boolean> checkDuplicateId(@RequestParam String userId) {
        logger.debug("Checking duplicate user ID: {}", userId);
        boolean exists = userService.findById(userId) != null;
        logger.debug("User ID {} exists: {}", userId, exists);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }
}
