package com.team.hotelmanagementapp.components;

import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.services.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@DependsOn("userServiceImpl")
public class DataInitializer {

    @Autowired
    @Lazy
    private UserService userService;

    @PostConstruct
    public void init() {
        if (userService.getByUsername("admin") == null) {
            User admin = new User("admin", "admin", "admin@gmail.com", User.Role.ADMIN, "Nguyễn", "Admin", "0123456789");
            User user = new User("user", "user", "user@gmail.com", User.Role.USER, "Trần Thị", "Na", "0967456615");

            userService.createOrUpdate(admin);
            userService.createOrUpdate(user);
        }
    }
}