package com.team.hotelmanagementapp.repositories;

import com.team.hotelmanagementapp.pojo.User;
import java.util.List;
import java.util.Map;

public interface UserRepository {
    
    List<User> filterUsers(Map<String, String> params);
    
    long countUsers(Map<String, String> params);

    User getByUsername(String username);
    
    User createOrUpdate(User user);
    
    boolean authUser(String username, String password);
}
