package com.team.hotelmanagementapp.controllers.api;

import com.team.hotelmanagementapp.services.FeedbackService;
import com.team.hotelmanagementapp.utils.Pagination;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedbacks")
@CrossOrigin
public class ApiFeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam Map<String, String> params) {
        try {
            return ResponseEntity.ok().body(new Pagination<>(
                    feedbackService.find(params), feedbackService.countFeedback(params), params));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra khi tải danh sách!");
        }
    }
    
}
