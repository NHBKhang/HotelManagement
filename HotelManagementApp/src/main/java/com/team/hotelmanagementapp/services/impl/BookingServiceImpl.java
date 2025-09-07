package com.team.hotelmanagementapp.services.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Payment;
import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.repositories.BookingRepository;
import com.team.hotelmanagementapp.services.BookingService;
import com.team.hotelmanagementapp.services.RoomService;
import com.team.hotelmanagementapp.services.UserService;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Override
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getById(int id) {
        return bookingRepository.getById(id);
    }

    @Override
    public List<Booking> find(Map<String, String> params) {
        return bookingRepository.findByUsername(params, null);
    }

    @Override
    public List<Booking> findByUsername(Map<String, String> params, String username) {
        return bookingRepository.findByUsername(params, username);
    }

    @Override
    public Booking createOrUpdate(Booking booking) {
//        if (booking.getId() != null) {
//            this.updateRoomStatus(booking);
//        }
        return bookingRepository.createOrUpdate(booking);
    }

    @Override
    public void cancelBooking(int bookingId) {
        Booking b = this.getById(bookingId);
        b.setStatus(Booking.Status.CANCELLED);
//        this.updateRoomStatus(b);
        this.createOrUpdate(b);
    }

    private void updateRoomStatus(Booking booking) {
        Room room = booking.getRoom();
        if (room != null) {
            switch (booking.getStatus()) {
                case CONFIRMED ->
                    room.setStatus(Room.Status.BOOKED);
                case CHECKED_IN ->
                    room.setStatus(Room.Status.OCCUPIED);
                case CHECKED_OUT ->
                    room.setStatus(Room.Status.CLEANING);
                case CANCELLED -> {
                    room.setStatus(Room.Status.AVAILABLE);
                }
            }
            roomService.createOrUpdate(room);
        }
    }

    @Override
    public long countBookings(Map<String, String> params) {
        return bookingRepository.countBookingsByUsername(params, null);
    }

    @Override
    public long countBookingsByUsername(Map<String, String> params, String username) {
        return bookingRepository.countBookingsByUsername(params, username);
    }

//    @Override
//    public List<Booking> findByStatus(Booking.Status status) {
//        return bookingRepository.findByStatus(status);
//    }
    @Override
    public Booking createByIdAndUsername(int bookingId, String username, Payment.Method method) {
        Booking b = this.getById(bookingId);

        b.setUser(this.userService.getByUsername(username));

        if (method == Payment.Method.VNPAY) {
            b.setStatus(Booking.Status.CONFIRMED);
        } else {
            b.setStatus(Booking.Status.PENDING);
        }

        return this.createOrUpdate(b);
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut, Integer excludeBookingId) {
        return bookingRepository.isRoomAvailable(roomId, checkIn, checkOut, excludeBookingId);
    }

    @Override
    public List<Booking> findBookingsByRoomAndDateRange(int roomId, LocalDate checkIn, LocalDate checkOut) {
        return bookingRepository.findBookingsByRoomAndDateRange(roomId, checkIn, checkOut);
    }
}
