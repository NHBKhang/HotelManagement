package com.team.hotelmanagementapp.controllers.api.dto;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Feedback;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FeedbackDTO {
    private Integer id;
    private Double rating;
    private String comment;
    private LocalDateTime createdAt;
    private BookingInfoDTO booking; // Chỉ chứa thông tin cần thiết

    public FeedbackDTO(Feedback feedback) {
        this.id = feedback.getId();
        this.rating = feedback.getRating();
        this.comment = feedback.getComment();
        this.createdAt = feedback.getCreatedAt();
        this.booking = new BookingInfoDTO(feedback.getBooking());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BookingInfoDTO getBooking() {
        return booking;
    }

    public void setBooking(BookingInfoDTO booking) {
        this.booking = booking;
    }


    public static class BookingInfoDTO {
        private Integer id;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;

        public BookingInfoDTO(Booking booking) {
            this.id = booking.getId();
            this.checkInDate = booking.getCheckInDate();
            this.checkOutDate = booking.getCheckOutDate();
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public LocalDate getCheckInDate() {
            return checkInDate;
        }

        public void setCheckInDate(LocalDate checkInDate) {
            this.checkInDate = checkInDate;
        }

        public LocalDate getCheckOutDate() {
            return checkOutDate;
        }

        public void setCheckOutDate(LocalDate checkOutDate) {
            this.checkOutDate = checkOutDate;
        }
    }
}
