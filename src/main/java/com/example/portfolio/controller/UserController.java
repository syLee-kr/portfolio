package com.example.portfolio.controller;

import com.example.portfolio.domain.Post;
import com.example.portfolio.domain.Post2;
import com.example.portfolio.domain.Users;
import com.example.portfolio.repository.UserRepository;
import com.example.portfolio.service.post.Post2Service;
import com.example.portfolio.service.post.PostService;
import com.example.portfolio.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final PostService postService;
    private final Post2Service post2Service;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, PostService postService, Post2Service post2Service, UserRepository userRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.postService = postService;
        this.post2Service = post2Service;
        this.userRepository = userRepository;
    }

    /**
     * 로그인 페이지 반환
     */
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        logger.info("Accessing the login page");
        if (session.getAttribute("user") != null) {
            logger.info("User already logged in. Redirecting to /main");
            return "redirect:/main";
        }
        return "login/login"; // 로그인 페이지로 이동
    }

    /**
     * 로그인 처리
     */
    @PostMapping("/main")
    public String login(@RequestParam String id, @RequestParam String pwd, HttpSession session, Model model) {
        logger.info("Login attempt for user ID: {}", id);
        Users user = userService.findById(id);
        if (user != null && passwordEncoder.matches(pwd, user.getPassword())) {
            session.setAttribute("user", user);
            logger.info("User {} logged in successfully", id);

            Post latestPost = postService.getLatestPost();
            model.addAttribute("post", latestPost);
            logger.debug("Latest post retrieved: {}", latestPost);

            List<Post2> post2List = post2Service.getRecentTenPosts(); // 수정
            model.addAttribute("post2List", post2List); // 리스트로 전달
            logger.debug("Recent ten Post2 items retrieved: {}", post2List.size());

            return "main/main"; // 리다이렉트 대신 직접 반환
        } else {
            logger.warn("Login failed for user ID: {}", id);
            model.addAttribute("loginFail", true);
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "login/login";
        }
    }

    /**
     * 메인 페이지 반환
     */
    @GetMapping("/main")
    public String mainPage(HttpSession session, Model model) {
        logger.info("Accessing the main page");
        Users currentUser = (Users) session.getAttribute("user");
        if (currentUser != null) {
            model.addAttribute("user", currentUser);
            logger.debug("Current user: {}", currentUser.getUserId());

            // 가장 최근의 Post 가져오기
            Post latestPost = postService.getLatestPost();
            model.addAttribute("post", latestPost);
            logger.debug("Latest post retrieved: {}", latestPost);

            // 모든 Post2 데이터 가져오기
            List<Post2> post2List = post2Service.getRecentTenPosts(); // 수정
            model.addAttribute("post2List", post2List); // 리스트로 전달
            logger.debug("Recent ten Post2 items retrieved: {}", post2List.size());

            return "main/main";
        } else {
            logger.warn("User not logged in. Redirecting to /login");
            return "redirect:/login";
        }
    }

    /**
     * 아이디 및 비밀번호 찾기 페이지 반환
     */
    @GetMapping("/find_username_password")
    public String findUP(){
        logger.info("Accessing the username/password find page");
        return "login/find";
    }

    /**
     * 아이디 찾기 처리
     */
    @PostMapping("/find/username")
    public String findUsername(@RequestParam String name, @RequestParam String email, Model model) {
        logger.info("Attempting to find username for name: {}, email: {}", name, email);
        Optional<Users> user = userRepository.findByNameAndEmail(name, email);
        if (user.isPresent()) {
            logger.info("Username found for name: {}, email: {}", name, email);
            model.addAttribute("message", "아이디는: " + user.get().getUserId());
        } else {
            logger.warn("No username found for name: {}, email: {}", name, email);
            model.addAttribute("message", "입력한 정보에 해당하는 아이디가 없습니다.");
        }
        return "login/find_result"; // 결과 페이지
    }

    /**
     * 비밀번호 찾기 처리
     */
    @PostMapping("/find/password")
    public String findPassword(@RequestParam String userId, @RequestParam String name, @RequestParam String email, Model model) {
        logger.info("Attempting to find password for user ID: {}, name: {}, email: {}", userId, name, email);
        Optional<Users> user = userRepository.findByUserIdAndNameAndEmail(userId, name, email);
        if (user.isPresent()) {
            logger.info("Password reset initiated for user ID: {}", userId);
            model.addAttribute("message", "비밀번호 재설정을 진행하세요.");
        } else {
            logger.warn("No account found for user ID: {}, name: {}, email: {}", userId, name, email);
            model.addAttribute("message", "입력한 정보에 해당하는 계정이 없습니다.");
        }
        return "login/find_result"; // 결과 페이지
    }

    /**
     * 프로필 페이지 반환
     */
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        logger.info("Accessing the profile page");
        Users currentUser = (Users) session.getAttribute("user");
        if (currentUser == null) {
            logger.warn("User not logged in. Redirecting to /login");
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }

        model.addAttribute("user", currentUser);
        logger.debug("Current user: {}", currentUser.getUserId());
        return "main/profile"; // 프로필 페이지 반환
    }

    /**
     * 로그아웃 처리
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        logger.info("User {} is logging out", session.getAttribute("user"));
        session.invalidate(); // 세션 무효화
        logger.info("User logged out successfully");
        return "redirect:/login"; // 로그인 페이지로 리다이렉트
    }

    /**
     * 프로필 수정 페이지 반환
     */
    @GetMapping("/edit-profile")
    public String editProfile(HttpSession session, Model model) {
        logger.info("Accessing the edit profile page");
        Users currentUser = (Users) session.getAttribute("user");
        if (currentUser == null) {
            logger.warn("User not logged in. Redirecting to /login");
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }

        model.addAttribute("user", currentUser);
        logger.debug("Current user: {}", currentUser.getUserId());
        return "main/edit-profile"; // 수정 페이지 반환
    }

    /**
     * 프로필 업데이트 처리
     */
    @PostMapping("/edit-profile")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam(required = false) String password,
            @RequestParam String birthday,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam(required = false) MultipartFile profileImage, // MultipartFile로 변경
            HttpSession session
    ) {
        logger.info("Updating profile for user ID: {}", ((Users) session.getAttribute("user")).getUserId());
        Users currentUser = (Users) session.getAttribute("user");
        if (currentUser == null) {
            logger.warn("User not logged in. Redirecting to /login");
            return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
        }

        String encodedPassword = (password != null && !password.isEmpty())
                ? passwordEncoder.encode(password)
                : currentUser.getPassword();
        logger.debug("Password encoding completed for user ID: {}", currentUser.getUserId());

        // 프로필 이미지 처리
        String profileImagePath = currentUser.getProfileImage(); // 기존 이미지 경로
        if (profileImage != null && !profileImage.isEmpty()) {
            logger.info("Processing profile image upload for user ID: {}", currentUser.getUserId());
            try {
                // 파일 저장 경로 설정
                String uploadDir = "uploads/profile-images/";
                String fileName = currentUser.getUserId() + "_" + profileImage.getOriginalFilename();
                Path filePath = Path.of(uploadDir, fileName);

                // 디렉토리 생성 (없을 경우)
                Files.createDirectories(filePath.getParent());
                logger.debug("Directory checked/created at: {}", filePath.getParent());

                // 파일 저장
                profileImage.transferTo(filePath.toFile());
                profileImagePath = filePath.toString(); // 저장된 파일 경로를 설정
                logger.info("Profile image uploaded successfully: {}", filePath.toString());
            } catch (IOException e) {
                logger.error("Error uploading profile image for user ID {}: {}", currentUser.getUserId(), e.getMessage(), e);
                return "redirect:/edit-profile?error=fileUpload"; // 파일 업로드 오류 처리
            }
        }

        // 사용자 정보 업데이트
        try {
            userService.updateUserDetails(
                    currentUser.getUserId(),
                    name,
                    encodedPassword,
                    birthday,
                    phone,
                    address,
                    profileImagePath
            );
            logger.info("User details updated successfully for user ID: {}", currentUser.getUserId());
        } catch (Exception e) {
            logger.error("Error updating user details for user ID {}: {}", currentUser.getUserId(), e.getMessage(), e);
            return "redirect:/edit-profile?error=updateFailed"; // 업데이트 오류 처리
        }

        // 세션에 업데이트된 사용자 정보 저장
        Users updatedUser = userService.findById(currentUser.getUserId());
        session.setAttribute("user", updatedUser);
        logger.debug("Session updated with new user details for user ID: {}", updatedUser.getUserId());

        return "redirect:/profile"; // 프로필 페이지로 리다이렉트
    }

}
