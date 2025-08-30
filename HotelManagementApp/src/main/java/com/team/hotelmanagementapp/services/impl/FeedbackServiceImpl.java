package com.team.hotelmanagementapp.services.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.hotelmanagementapp.pojo.Feedback;
import com.team.hotelmanagementapp.repositories.FeedbackRepository;
import com.team.hotelmanagementapp.services.FeedbackService;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public List<Feedback> findAll() {
        return this.feedbackRepository.find(null);
    }

    @Override
    public Feedback findById(int id) {
        return this.feedbackRepository.findById(id);
    }

    @Override
    public List<Feedback> findByUser(int userId, Map<String, String> params) {
        if (params == null) {
            return this.feedbackRepository.findByUser(userId);
        }

        Map<String, String> paramsCopy = new java.util.HashMap<>(params);
        paramsCopy.put("userId", String.valueOf(userId));
        return this.feedbackRepository.find(paramsCopy);
    }

    @Override
    public List<Feedback> findByBooking(int bookingId) {
        return this.feedbackRepository.findByBooking(bookingId);
    }

    @Override
    public Feedback createFeedback(Feedback feedback) {
        // Validate rating range
        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            throw new IllegalArgumentException("Rating phải nằm trong khoảng 1-5");
        }

        // Validate booking exists and user is authorized
        if (feedback.getBooking() == null) {
            throw new IllegalArgumentException("Booking không được để trống");
        }

        if (feedback.getUser() == null) {
            throw new IllegalArgumentException("User không được để trống");
        }

        return this.feedbackRepository.createOrUpdate(feedback);
    }

    @Override
    public Feedback updateFeedback(int feedbackId, Feedback feedback) {
        Feedback existing = this.feedbackRepository.findById(feedbackId);
        if (existing == null) {
            throw new IllegalArgumentException("Feedback không tồn tại");
        }

        // Preserve creation timestamp
        feedback.setCreatedAt(existing.getCreatedAt());

        // Only allow updating specific fields
        existing.setRating(feedback.getRating());
        existing.setComment(feedback.getComment());

        return this.feedbackRepository.createOrUpdate(existing);
    }

    @Override
    public void deleteFeedback(int feedbackId) {
        Feedback feedback = this.feedbackRepository.findById(feedbackId);
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback không tồn tại");
        }

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
