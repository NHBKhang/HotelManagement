package com.team.hotelmanagementapp.services;

import java.util.List;
import java.util.Map;
import com.team.hotelmanagementapp.pojo.Feedback;

public interface FeedbackService {

    List<Feedback> findAll();

    List<Feedback> find(Map<String, String> params);

    Feedback getById(int id);

    List<Feedback> findByUser(int userId, Map<String, String> params);

    List<Feedback> findByBooking(int bookingId, Map<String, String> params);

    Feedback createOrUpdate(Feedback feedback);

    void delete(int feedbackId);

    double getAverageRating();

    long countFeedback(Map<String, String> params);

    List<Feedback> findByRatingHigherThan(double rating);
}
