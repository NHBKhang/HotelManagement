package com.team.hotelmanagementapp.components;

import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.pojo.RoomType;
import com.team.hotelmanagementapp.pojo.User;
import com.team.hotelmanagementapp.repositories.BookingRepository;
import com.team.hotelmanagementapp.repositories.RoomRepository;
import com.team.hotelmanagementapp.repositories.RoomTypeRepository;
import com.team.hotelmanagementapp.services.UserService;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@DependsOn("userServiceImpl")
public class DataInitializer {

    @Autowired
    @Lazy
    private UserService userService;
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private BookingRepository bookingRepository;

    @PostConstruct
    public void init() {
        // Create users
        User admin = null, customer = null;
        if (userService.getByUsername("admin") == null) {
            admin = new User("admin", "admin", "admin@gmail.com", User.Role.ADMIN, "Nguyễn", "Admin", "0123456789");
            customer = new User("customer", "customer", "customer@gmail.com", User.Role.CUSTOMER, "Trần Thị", "Na", "0967456615");

            userService.createOrUpdate(admin);
            userService.createOrUpdate(customer);
        } else {
            admin = userService.getByUsername("admin");
            customer = userService.getByUsername("customer");
        }
        
        // Create room types
        if (roomTypeRepository.findAll().isEmpty()) {
            RoomType standard = roomTypeRepository.save(new RoomType(null, "Standard", "Comfortable room with basic amenities", 500000.0, 2));
            RoomType deluxe = roomTypeRepository.save(new RoomType(null, "Deluxe", "Spacious room with premium amenities", 800000.0, 3));
            RoomType suite = roomTypeRepository.save(new RoomType(null, "Suite", "Luxury suite with separate living area", 1200000.0, 4));
            
            // Create rooms
            for (int i = 1; i <= 10; i++) {
                RoomType type = i <= 6 ? standard : (i <= 8 ? deluxe : suite);
                roomRepository.save(new Room(null, "10" + String.format("%02d", i), type, Room.Status.AVAILABLE));
            }
            
            // Create sample bookings
            Room room1 = roomRepository.findAll().get(0);
            Room room2 = roomRepository.findAll().get(1);
            
            bookingRepository.save(new Booking(null, customer, room1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), Booking.Status.CONFIRMED, "Early check-in requested"));
            bookingRepository.save(new Booking(null, customer, room2, LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), Booking.Status.PENDING, null));
        }
    }
}