package com.team.hotelmanagementapp.services;

import com.team.hotelmanagementapp.pojo.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User getUserByUsername(String username);
}
