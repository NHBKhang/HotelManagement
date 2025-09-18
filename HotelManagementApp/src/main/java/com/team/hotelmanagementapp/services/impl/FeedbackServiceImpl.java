package com.team.hotelmanagementapp.services.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.hotelmanagementapp.pojo.Feedback;
import com.team.hotelmanagementapp.repositories.FeedbackRepository;
import com.team.hotelmanagementapp.services.FeedbackService;
import java.util.HashMap;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public List<Feedback> findAll() {
        return this.feedbackRepository.find(null);
    }

    @Override
    public List<Feedback> find(Map<String, String> params) {
        return this.feedbackRepository.find(params);
    }

    @Override
    public Feedback getById(int id) {
        return this.feedbackRepository.getById(id);
    }

    @Override
    public List<Feedback> findByUser(int userId, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("userId", String.valueOf(userId));
        return this.feedbackRepository.find(params);
    }

    @Override
    public List<Feedback> findByBooking(int bookingId, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("bookingId", String.valueOf(bookingId));
        return this.feedbackRepository.find(params);
    }

    @Override
    public Feedback createOrUpdate(Feedback feedback) {
        if (feedback.getRating() < 0.5 || feedback.getRating() > 5.0) {
            throw new IllegalArgumentException("Rating phải nằm trong khoảng 1-5");
        }

        if (feedback.getBooking() == null) {
            throw new IllegalArgumentException("Booking không được để trống");
        }

        if (feedback.getUser() == null) {
            throw new IllegalArgumentException("User không được để trống");
        }

        return this.feedbackRepository.createOrUpdate(feedback);
    }

    @Override
    public void delete(int feedbackId) {
        this.feedbackRepository.delete(feedbackId);
    }

    @Override
    public double getAverageRating() {
        return this.feedbackRepository.getAverageRating();
    }

    @Override
    public long countFeedback(Map<String, String> params) {
        return this.feedbackRepository.countFeedback(params);
    }

    @Override
    public List<Feedback> findByRatingHigherThan(double rating) {
        return this.feedbackRepository.findByRatingHigherThan(rating);
    }
}
