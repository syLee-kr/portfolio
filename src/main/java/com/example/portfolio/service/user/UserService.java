package com.example.portfolio.service.user;

import com.example.portfolio.domain.Users;

import java.util.List;

public interface UserService {

    // 1. 유저 이름으로 전체 정보 가져오기
    List<Users> getUserByName(String name);

    // 2. 전체 유저들의 상태 가져오기
    List<Object[]> getAllUserStatuses();

    List<Users> findAllUsers(); // 가입된 모든 유저 가져오기
    // 3. 특정 유저의 상태 변경
    void updateUserStatus(String userId, Users.Status status);

    // 4. 특정 유저의 설정 수정
    void updateUserDetails(String userId, String name, String password, String birthday,
                              String phone, String address, String profileImage);

    Users findById(String userId);

    void save(Users user);

    List<Users> getOtherUsers(String currentUserId);
}