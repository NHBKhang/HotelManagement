package com.team.hotelmanagementapp.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.team.hotelmanagementapp.pojo.Booking;

public interface BookingRepository {

    List<Booking> findAll();

    Booking findById(int id);

    List<Booking> findByUser(int userId, Map<String, String> params);

    Booking save(Booking booking);

    void delete(int id);

    long countBookings(Map<String, String> params);

    List<Booking> findByStatus(Booking.Status status);

    // Room availability methods
    List<Booking> findBookingsByRoom(int roomId);
    List<Booking> findBookingsByRoomAndDateRange(int roomId, LocalDate checkIn, LocalDate checkOut);
    boolean isRoomBooked(int roomId, LocalDate checkIn, LocalDate checkOut);
}