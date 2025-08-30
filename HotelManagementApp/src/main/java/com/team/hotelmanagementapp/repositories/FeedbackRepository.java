package com.team.hotelmanagementapp.repositories;

import java.util.List;
import java.util.Map;

import com.team.hotelmanagementapp.controllers.api.dto.FeedbackDTO;
import com.team.hotelmanagementapp.pojo.Feedback;

public interface FeedbackRepository {

    List<FeedbackDTO> find(Map<String, String> params);

    Feedback findById(int id);

    List<FeedbackDTO> findByUser(int userId);

    List<FeedbackDTO> findByBooking(int bookingId);

    Feedback createOrUpdate(Feedback feedback);

    void delete(int id);

    void delete(List<Integer> ids);

    long countFeedback(Map<String, String> params);

    double getAverageRating();

    List<Feedback> findByRatingHigherThan(double rating);
}
