package com.team.hotelmanagementapp.repositories;

import java.util.List;
import java.util.Map;
import com.team.hotelmanagementapp.pojo.Feedback;

public interface FeedbackRepository {

    List<Feedback> find(Map<String, String> params);

    Feedback getById(int id);

    Feedback createOrUpdate(Feedback feedback);

    void delete(int id);

    void delete(List<Integer> ids);

    long countFeedback(Map<String, String> params);

    double getAverageRating();

    List<Feedback> findByRatingHigherThan(double rating);
}
