package com.team.hotelmanagementapp.controllers.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.hotelmanagementapp.components.JwtService;
import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.services.UserService;
import com.team.hotelmanagementapp.utils.Pagination;
import com.team.hotelmanagementapp.utils.RequestValidation;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiUserController {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            if (this.userService.authUser(user.getUsername(), user.getPassword())) {
                String token = this.jwtService.generateTokenLogin(user.getUsername());
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Sai mật khẩu!");
            }
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Không tìm thấy người dùng!");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi máy chủ: " + ex.getMessage());
        }
    }

    @GetMapping(path = "/current-user")
    public ResponseEntity<Object> getCurrentUser(HttpServletRequest request) {
        try {
            RequestValidation val = RequestValidation.getUserFromRequest(request, userService, jwtService);

            if(val.getUser() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(val.getMessage());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("user", val.getUser());

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi máy chủ: " + ex.getMessage());
        }
    }

    @GetMapping(path = "/users")
    public ResponseEntity<?> list(@RequestParam Map<String, String> params) {
        try {
            List<User> users = this.userService.find(params);
            long totalUsers = this.userService.countUsers(params);
            return ResponseEntity.ok(new Pagination<>(users, totalUsers, params));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi tải danh sách người dùng!");
        }
    }

    @PostMapping(path = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@ModelAttribute("user") User user) {
        try {
            return new ResponseEntity<>(this.userService.createOrUpdate(user), HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Có lỗi xảy ra khi tạo mới người dùng!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PatchMapping(path = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@ModelAttribute("user") User user) {
        return new ResponseEntity<>(this.userService.createOrUpdate(user), HttpStatus.OK);
    }
}
