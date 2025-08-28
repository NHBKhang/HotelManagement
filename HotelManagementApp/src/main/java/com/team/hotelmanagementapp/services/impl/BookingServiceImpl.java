package com.team.hotelmanagementapp.services.impl;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.repositories.BookingRepository;
import com.team.hotelmanagementapp.services.BookingService;
import com.team.hotelmanagementapp.services.RoomService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private RoomService roomService;

    @Override
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking findById(int id) {
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> findByUser(int userId, Map<String, String> params) {
        return bookingRepository.findByUser(userId, params);
    }

    @Override
    public Booking createBooking(Booking booking) {
        booking.setStatus(Booking.Status.PENDING);
        
        // Update room status to BOOKED
        Room room = booking.getRoom();
        if (room != null && room.getStatus() == Room.Status.AVAILABLE) {
            room.setStatus(Room.Status.BOOKED);
            roomService.save(room);
        }
        
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBookingStatus(int bookingId, Booking.Status status) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking != null) {
            Booking.Status oldStatus = booking.getStatus();
            booking.setStatus(status);
            
            // Update room status based on booking status
            Room room = booking.getRoom();
            if (room != null) {
                switch (status) {
                    case CONFIRMED:
                        room.setStatus(Room.Status.BOOKED);
                        break;
                    case CHECKED_IN:
                        room.setStatus(Room.Status.OCCUPIED);
                        break;
                    case CHECKED_OUT:
                        room.setStatus(Room.Status.CLEANING);
                        break;
                    case CANCELLED:
                        if (oldStatus != Booking.Status.CHECKED_OUT) {
                            room.setStatus(Room.Status.AVAILABLE);
                        }
                        break;
                }
                roomService.save(room);
            }
            
            return bookingRepository.save(booking);
        }
        return null;
    }

    @Override
    public void cancelBooking(int bookingId) {
        updateBookingStatus(bookingId, Booking.Status.CANCELLED);
    }

    @Override
    public long countBookings(Map<String, String> params) {
        return bookingRepository.countBookings(params);
    }

    @Override
    public List<Booking> findByStatus(Booking.Status status) {
        return bookingRepository.findByStatus(status);
    }
}