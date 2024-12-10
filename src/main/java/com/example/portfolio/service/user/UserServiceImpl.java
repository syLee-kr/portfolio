package com.example.portfolio.service.user;

import com.example.portfolio.domain.Users;
import com.example.portfolio.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepo;

    @Autowired
    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public List<Users> getUserByName(String name) {
        return userRepo.findByName(name);
    }

    @Override
    public List<Object[]> getAllUserStatuses() {
        return userRepo.findAllUsersWithStatus();
    }

    @Override
    public void updateUserStatus(String userId, Users.Status status) {
        userRepo.updateUserStatus(userId, status);
    }

    @Override
    public void updateUserDetails(String userId, String name, String password, String birthday, String phone, String address, String profileImage) {
        userRepo.updateUserDetails(userId, name, password, birthday, phone, address, profileImage);
    }

    @Override
    public Users findById(String userId) {
        return userRepo.findById(userId).orElse(null);
    }

    @Override
    public void save(Users user) {
        userRepo.save(user); // Spring Data JPA의 save 메서드 호출
    }

    @Override
    public List<Users> getOtherUsers(String currentUserId) {
        return userRepo.findByUserIdNot(currentUserId);
    }

    @Transactional
    public List<Users> findAllUsers() {
        List<Users> users = userRepo.findAll();
        users.forEach(user -> Hibernate.initialize(user.getEvents())); // 명시적 초기화
        return users;
    }
}
