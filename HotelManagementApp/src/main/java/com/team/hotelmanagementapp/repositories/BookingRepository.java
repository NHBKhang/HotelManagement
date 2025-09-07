package com.team.hotelmanagementapp.repositories;

import java.util.List;
import java.util.Map;
import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Room;
import java.time.LocalDate;

public interface BookingRepository {

    List<Booking> findAll();

    Booking getById(int id);

    List<Booking> findByUsername(Map<String, String> params, String username);

    Booking createOrUpdate(Booking booking);

    void delete(int id);

    long countBookingsByUsername(Map<String, String> params, String username);

    List<Booking> findBookingsByRoomAndDateRange(int roomId, LocalDate checkIn, LocalDate checkOut);

    boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut, Integer excludeBookingId);

    List<Booking> find(Map<String, String> params);

    long countBookings(Map<String, String> params);

    List<Booking> findRecentBookingsByRoom(Room room, int limit);
}
