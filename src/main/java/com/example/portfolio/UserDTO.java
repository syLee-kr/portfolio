package com.example.portfolio;

import com.example.portfolio.domain.Users;
import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String name;

    public UserDTO(Users user) {
        this.id = user.getUserId();
        this.name = user.getName();
    }
    // getters and setters
}
