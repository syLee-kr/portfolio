package com.example.portfolio.repository;

import com.example.portfolio.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {

    // 이메일로 사용자 조회
    Optional<Users> findByEmail(String email);

    Optional<Users> findByNameAndEmail(String name, String email);

    Optional<Users> findByUserIdAndNameAndEmail(String userId, String name, String email);

    Optional<Users> findById(String userId); // 이미 존재한다면 생략 가능

    @Query("SELECT u FROM Users u WHERE u.userId <> :currentUserId")
    List<Users> findByUserIdNot(@Param("currentUserId") String currentUserId);

    // 1. 유저 이름으로 전체 정보 가져오기
    List<Users> findByName(String name);

    // 2. 전체 유저들의 상태 가져오기
    @Query("SELECT u.userId, u.name, u.status FROM Users u")
    List<Object[]> findAllUsersWithStatus();

    // 3. 특정 유저의 상태 변경하기
    @Transactional
    @Modifying
    @Query("UPDATE Users u SET u.status = :status WHERE u.userId = :userId")
    void updateUserStatus(@Param("userId") String userId, @Param("status") Users.Status status);

    // 4. 유저의 설정(전체 정보) 수정 후 저장
    @Transactional
    @Modifying
    @Query("UPDATE Users u SET u.name = :name, u.password = :password, u.birthday = :birthday, " +
            "u.phone = :phone, u.address = :address, u.profileImage = :profileImage " +
            "WHERE u.userId = :userId")
    void updateUserDetails(@Param("userId") String userId,
                           @Param("name") String name,
                           @Param("password") String password,
                           @Param("birthday") String birthday,
                           @Param("phone") String phone,
                           @Param("address") String address,
                           @Param("profileImage") String profileImage);
}

