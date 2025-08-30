package com.team.hotelmanagementapp.controllers.api;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team.hotelmanagementapp.components.JwtService;
import com.team.hotelmanagementapp.pojo.Service;
import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.services.ServiceService;
import com.team.hotelmanagementapp.services.UserService;
import com.team.hotelmanagementapp.utils.Pagination;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/services")
@CrossOrigin
public class ApiServiceController {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam Map<String, String> params) {
        try {
            List<Service> services = serviceService.find(params);
            long totalServices = serviceService.countServices(params);
            return ResponseEntity.ok(new Pagination<>(services, totalServices, params));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tải danh sách dịch vụ!"));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> listActive(@RequestParam Map<String, String> params) {
        try {
            params.put("active", "true");
            List<Service> services = serviceService.find(params);
            long totalServices = serviceService.countServices(params);
            return ResponseEntity.ok(new Pagination<>(services, totalServices, params));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tải danh sách dịch vụ hoạt động!"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getService(@PathVariable int id) {
        try {
            Service service = serviceService.getById(id);
            if (service == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy dịch vụ với ID: " + id));
            }
            return ResponseEntity.ok(service);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi lấy thông tin dịch vụ!"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createService(@RequestBody Service service, HttpServletRequest request) {
        try {
            // Check authentication and authorization
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token không hợp lệ"));
            }

            if (!user.getRole().equals(User.Role.ADMIN) && !user.getRole().equals(User.Role.MANAGER)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền tạo dịch vụ"));
            }

            // Validate required fields
            if (service.getName() == null || service.getName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Tên dịch vụ không được để trống"));
            }

            if (service.getPrice() == null || service.getPrice() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Giá dịch vụ phải lớn hơn 0"));
            }

            // Set default values
            if (service.getActive() == null) {
                service.setActive(true);
            }
            if (service.getCode() == null || service.getCode().trim().isEmpty()) {
                service.setCode(generateServiceCode());
            }
            service.setCreatedAt(LocalDateTime.now());

            Service created = serviceService.createOrUpdate(service);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Dịch vụ đã được tạo thành công");
            response.put("service", created);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tạo dịch vụ!"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable int id, @RequestBody Service service, HttpServletRequest request) {
        try {
            // Check authentication and authorization
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token không hợp lệ"));
            }

            if (!user.getRole().equals(User.Role.ADMIN) && !user.getRole().equals(User.Role.MANAGER)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền cập nhật dịch vụ"));
            }

            Service existing = serviceService.getById(id);
            if (existing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy dịch vụ"));
            }

            // Update allowed fields
            boolean hasChanges = false;
            if (service.getName() != null && !service.getName().trim().isEmpty()) {
                existing.setName(service.getName());
                hasChanges = true;
            }
            if (service.getDescription() != null) {
                existing.setDescription(service.getDescription());
                hasChanges = true;
            }
            if (service.getPrice() != null && service.getPrice() > 0) {
                existing.setPrice(service.getPrice());
                hasChanges = true;
            }
            if (service.getActive() != null) {
                existing.setActive(service.getActive());
                hasChanges = true;
            }

            if (!hasChanges) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Không có thay đổi nào được thực hiện"));
            }

            Service updated = serviceService.createOrUpdate(existing);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Dịch vụ đã được cập nhật thành công");
            response.put("service", updated);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi cập nhật dịch vụ!"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable int id, HttpServletRequest request) {
        try {
            // Check authentication and authorization
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token không hợp lệ"));
            }

            if (!user.getRole().equals(User.Role.ADMIN) && !user.getRole().equals(User.Role.MANAGER)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền xóa dịch vụ"));
            }

            Service service = serviceService.getById(id);
            if (service == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy dịch vụ"));
            }

            // Deactivate service instead of hard delete
            service.setActive(false);
            serviceService.createOrUpdate(service);

            return ResponseEntity.ok(Map.of("message", "Dịch vụ đã được vô hiệu hóa thành công"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi xóa dịch vụ!"));
        }
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activateService(@PathVariable int id, HttpServletRequest request) {
        try {
            // Check authentication and authorization
            User user = getCurrentUser(request);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token không hợp lệ"));
            }

            if (!user.getRole().equals(User.Role.ADMIN) && !user.getRole().equals(User.Role.MANAGER)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền kích hoạt dịch vụ"));
            }

            Service service = serviceService.getById(id);
            if (service == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy dịch vụ"));
            }

            service.setActive(true);
            Service activated = serviceService.createOrUpdate(service);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Dịch vụ đã được kích hoạt thành công");
            response.put("service", activated);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi kích hoạt dịch vụ!"));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getServiceStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            Map<String, String> activeParams = new HashMap<>();
            activeParams.put("active", "true");
            activeParams.put("page", "1");
            activeParams.put("pageSize", "1000");

            stats.put("totalServices", serviceService.countServices(null));
            stats.put("activeServices", serviceService.countServices(activeParams));

            List<Service> activeServices = serviceService.find(activeParams);
            double avgPrice = activeServices.stream()
                    .filter(s -> s.getPrice() != null)
                    .mapToDouble(Service::getPrice)
                    .average()
                    .orElse(0.0);

            stats.put("averagePrice", Math.round(avgPrice * 100.0) / 100.0);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Có lỗi xảy ra khi tải thống kê dịch vụ!"));
        }
    }

    private User getCurrentUser(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (token == null || jwtService.getUsernameFromToken(token) == null) {
                return null;
            }

            String username = jwtService.getUsernameFromToken(token);
            return userService.getByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }

    private String generateServiceCode() {
        // Generate a simple service code like SVC001, SVC002, etc.
        int nextId = (int) (serviceService.countServices(null) + 1);
        return "SVC" + String.format("%03d", nextId);
    }
}