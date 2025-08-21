package com.team.hotelmanagementapp.repositories;

import com.team.hotelmanagementapp.pojo.User;

public interface UserRepository {

    User getUserByUsername(String username);
}
