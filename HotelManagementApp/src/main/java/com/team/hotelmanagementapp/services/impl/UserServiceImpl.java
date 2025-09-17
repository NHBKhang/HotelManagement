package com.team.hotelmanagementapp.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.repositories.UserRepository;
import com.team.hotelmanagementapp.services.UserService;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public List<User> findAll() {
        return this.userRepo.findAll();
    }

    @Override
    public List<User> find(Map<String, String> params) {
        return this.userRepo.find(params);
    }

    @Override
    public long countUsers(Map<String, String> params) {
        return this.userRepo.countUsers(params);
    }

    @Override
    public User getByUsername(String username) {
        return this.userRepo.getByUsername(username);
    }

    @Override
    public User getById(int id) {
        return this.userRepo.getById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = this.getByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Invalid username");
        }

        if (u.getStatus() == User.Status.BANNED) {
            throw new UsernameNotFoundException("User is banned!");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + u.getRole().toString()));

        return new org.springframework.security.core.userdetails.User(
                u.getUsername(), u.getPassword(), authorities);
    }

    @Override
    public User createOrUpdate(User user) {
        if (user.getPassword() != null) {
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        }

        if (user.getFile() != null && !user.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(user.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                user.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Error uploading file to Cloudinary", ex);
            }
        }
        if (user.getRole() == null) {
            user.setRole(User.Role.CUSTOMER);
        }

        if (user.getStatus() == null) {
            user.setStatus(User.Status.ACTIVE);
        }

        return this.userRepo.createOrUpdate(user);
    }

    @Override
    public void delete(int id) {
        this.userRepo.delete(id);
    }

    @Override
    public void delete(List<Integer> ids) {
        this.userRepo.delete(ids);
    }

    @Override
    public boolean authUser(String username, String password) {
        return this.userRepo.authUser(username, password);
    }
}
