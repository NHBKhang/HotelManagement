package com.team.hotelmanagementapp.services;

import java.util.List;
import java.util.Map;

import com.team.hotelmanagementapp.controllers.api.dto.FeedbackDTO;
import com.team.hotelmanagementapp.pojo.Feedback;

public interface FeedbackService {

    List<FeedbackDTO> findAll();

    Feedback findById(int id);

    List<FeedbackDTO> findByUser(int userId, Map<String, String> params);

    List<FeedbackDTO> findByBooking(int bookingId);

    Feedback createFeedback(Feedback feedback);

    Feedback updateFeedback(int feedbackId, Feedback feedback);

    void deleteFeedback(int feedbackId);

    double getAverageRating();

    long countFeedback(Map<String, String> params);

    List<Feedback> findByRatingHigherThan(double rating);
}
