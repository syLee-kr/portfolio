package com.example.portfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /*

    private final UserServiceImpl userService;

    @Autowired
    public SecurityConfig(UserServiceImpl userService) {
        this.userService = userService;
    }

    *//**
     * UserDetailsService 구현: 데이터베이스에서 사용자 정보 로드
     *//*
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Users user = userService.findById(username); // UserServiceImpl의 findById 호출
            if (user == null) {
                throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
            }

            // UserDetails 객체 반환
            return User.builder()
                    .username(user.getUserId())
                    .password(user.getPassword()) // 암호화된 비밀번호 사용
                    .authorities(Collections.singletonList(() -> user.getRole())) // 권한 설정
                    .build();
        };
    }

    *//**
     * Spring Security 필터 체인 설정
     *//*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**") // /api/** 경로는 CSRF 보호 무시
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/main", "/login", "/css/**", "/js/**", "/favicon.ico", "/error", "/images/**", "/join", "/join/**").permitAll() // 누구나 접근 가능
                        .requestMatchers("/main").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN") // ADMIN 권한만 접근 가능
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .formLogin(form -> form
                        .loginPage("/login") // 사용자 정의 로그인 페이지
                        .defaultSuccessUrl("/main", true) // 로그인 성공 후 이동할 페이지
                        .permitAll() // 로그인 페이지는 접근 허용
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 URL
                        .logoutSuccessUrl("/login?logout") // 로그아웃 성공 후 이동할 페이지
                        .invalidateHttpSession(true) // 세션 무효화
                        .clearAuthentication(true) // 인증 정보 제거
                        .permitAll() // 로그아웃 URL 접근 허용
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecret") // Remember-Me 쿠키를 위한 고유 키
                        .tokenValiditySeconds(7 * 24 * 60 * 60) // Remember-Me 쿠키 유효기간 7일
                        .userDetailsService(userDetailsService()) // UserDetailsService 설정
                );

        return http.build();
    }

/*
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }*/
}
