package com.team.hotelmanagementapp.utils;

import com.team.hotelmanagementapp.components.JwtService;
import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.services.UserService;
import jakarta.servlet.http.HttpServletRequest;

public class RequestValidation {

    private final User user;
    private final String message;

    public RequestValidation(User user, String message) {
        this.user = user;
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public static RequestValidation getUserFromRequest(HttpServletRequest request,
            UserService userService, JwtService jwtService) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return new RequestValidation(null, "Thiếu token xác thực");
        }
        token = token.substring(7);
        String username = jwtService.getUsernameFromToken(token);
        if (username == null) {
            return new RequestValidation(null, "Token không hợp lệ hoặc đã hết hạn");
        }
        User user = userService.getByUsername(username);
        if (user == null) {
            return new RequestValidation(null, "Không tìm thấy người dùng");
        }
        return new RequestValidation(user, null);
    }
}
