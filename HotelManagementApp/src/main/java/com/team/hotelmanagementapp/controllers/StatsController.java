package com.team.hotelmanagementapp.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.hotelmanagementapp.pojo.Booking;
import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.pojo.Service;
import com.team.hotelmanagementapp.pojo.ServiceBooking;
import com.team.hotelmanagementapp.services.BookingService;
import com.team.hotelmanagementapp.services.RoomService;
import com.team.hotelmanagementapp.services.ServiceBookingService;
import com.team.hotelmanagementapp.services.ServiceService;
import com.team.hotelmanagementapp.services.StatsService;
import com.team.hotelmanagementapp.services.UserService;

@Controller
@ControllerAdvice
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private ServiceService serviceService;
    
    @Autowired
    private ServiceBookingService serviceBookingService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public String statisticIndex(
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Model model) throws JsonProcessingException {

        if (start == null) {
            start = LocalDate.now().withDayOfYear(1);
        }
        if (end == null) {
            end = LocalDate.now();
        }

        double totalRevenue = statsService.getTotalRevenue(start, end);
        long totalBookings = statsService.countBookings(start, end);
        long totalRooms = statsService.countRooms();
        long totalServiceBookings = statsService.countServiceBookings(start, end);

        Map<String, Object> revenueStats = statsService.getRevenueStats(start, end);
        Map<String, Object> bookingStatusStats = statsService.getBookingStatusStats(start, end);
        Map<String, Object> newUsersStats = statsService.getNewUsersStats(start, end);

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("totalServiceBookings", totalServiceBookings);

        model.addAttribute("revenueStats", revenueStats);
        model.addAttribute("bookingStatusStats", bookingStatusStats);
        model.addAttribute("newUsersStats", newUsersStats);

        model.addAttribute("start", start);
        model.addAttribute("end", end);

        return "stats/index";
    }

    @GetMapping("/revenue")
    public String revenueStatistics(
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Model model) throws JsonProcessingException {

        if (start == null || end == null) {
            LocalDate now = LocalDate.now();
            end = now.withDayOfMonth(now.lengthOfMonth());
            start = now.minusMonths(5).withDayOfMonth(1);   
        }

        Map<String, Double> revenueByMonth = statsService.getRevenueByMonth(start, end);
        double totalRevenue = statsService.getTotalRevenue(start, end);

        long totalInvoices = statsService.countInvoices(start, end);

        double avgInvoice = totalInvoices > 0 ? totalRevenue / totalInvoices : 0;

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalInvoices", totalInvoices);
        model.addAttribute("avgInvoice", avgInvoice);
        model.addAttribute("revenueByMonth", revenueByMonth);
        model.addAttribute("revenueByMonthJson", objectMapper.writeValueAsString(revenueByMonth));
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        return "stats/revenue";
    }

    @GetMapping("/bookings")
    public String bookingStatistics(
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Model model) {

        if (start == null) {
            start = LocalDate.now().withDayOfYear(1);
        }
        if (end == null) {
            end = LocalDate.now();
        }

        long totalBookings = statsService.countBookings(start, end);
        Map<String, Object> bookingStatusStats = statsService.getBookingStatusStats(start, end);
        
        List<Booking> recentBookings = statsService.getRecentBookings(10);

        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("bookingStatusStats", bookingStatusStats);
        model.addAttribute("recentBookings", recentBookings);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        return "stats/bookings";
    }

    @GetMapping("/rooms")
    public String roomStatistics(Model model) {
        long totalRooms = statsService.countRooms();
        List<Room> rooms = roomService.findAll();

        Map<String, Long> occupancyStats = statsService.getRoomsByStatus();

        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("occupancyStats", occupancyStats);
        model.addAttribute("rooms", rooms);

        return "stats/rooms";
    }

    @GetMapping("/service")
    public String serviceStatistics(
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Model model) {

        if (start == null) {
            start = LocalDate.now().withDayOfYear(1);
        }
        if (end == null) {
            end = LocalDate.now();
        }

        long totalServiceBookings = statsService.countServiceBookings(start, end);
        List<Service> services = statsService.getAllServices();
        List<ServiceBooking> recentServiceBookings = statsService.getRecentServiceBookings(10);

        double totalServiceRevenue = statsService.getTotalServiceRevenue(start, end);

        model.addAttribute("totalServiceBookings", totalServiceBookings);
        model.addAttribute("totalServiceRevenue", totalServiceRevenue);
        model.addAttribute("services", services);
        model.addAttribute("recentServiceBookings", recentServiceBookings);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        return "stats/service";
    }

    // API Endpoints for dashboard reporting
    @GetMapping("/api/overview")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardOverview(
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        if (start == null) {
            start = LocalDate.now().withDayOfYear(1);
        }
        if (end == null) {
            end = LocalDate.now();
        }

        Map<String, Object> overview = new HashMap<>();
        overview.put("totalRevenue", statsService.getTotalRevenue(start, end));
        overview.put("totalBookings", statsService.countBookings(start, end));
        overview.put("totalRooms", statsService.countRooms());
        overview.put("totalServiceBookings", statsService.countServiceBookings(start, end));
        overview.put("totalInvoices", statsService.countInvoices(start, end));

        return ResponseEntity.ok(overview);
    }

    @GetMapping("/api/revenue")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRevenueData(
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        if (start == null) {
            start = LocalDate.now().minusMonths(6);
        }
        if (end == null) {
            end = LocalDate.now();
        }

        Map<String, Object> revenueData = new HashMap<>();
        revenueData.put("totalRevenue", statsService.getTotalRevenue(start, end));
        revenueData.put("revenueByMonth", statsService.getRevenueByMonth(start, end));
        revenueData.put("revenueStats", statsService.getRevenueStats(start, end));

        return ResponseEntity.ok(revenueData);
    }

    @GetMapping("/api/bookings")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBookingData(
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        if (start == null) {
            start = LocalDate.now().withDayOfYear(1);
        }
        if (end == null) {
            end = LocalDate.now();
        }

        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("totalBookings", statsService.countBookings(start, end));
        bookingData.put("bookingStatusStats", statsService.getBookingStatusStats(start, end));

        return ResponseEntity.ok(bookingData);
    }

    @GetMapping("/api/rooms")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRoomData() {
        List<Room> rooms = roomService.findAll();
        
        Map<String, Object> roomData = new HashMap<>();
        roomData.put("totalRooms", rooms.size());
        
        Map<String, Long> statusDistribution = statsService.getRoomsByStatus();
        roomData.put("statusDistribution", statusDistribution);
        roomData.put("occupancyRate", rooms.isEmpty() ? 0 :
            (double) statusDistribution.get("occupied") / rooms.size() * 100);

        return ResponseEntity.ok(roomData);
    }

    @GetMapping("/api/services")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getServiceData(
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        if (start == null) {
            start = LocalDate.now().withDayOfYear(1);
        }
        if (end == null) {
            end = LocalDate.now();
        }

        Map<String, Object> serviceData = new HashMap<>();
        serviceData.put("totalServiceBookings", statsService.countServiceBookings(start, end));
        
        double totalServiceRevenue = statsService.getTotalServiceRevenue(start, end);
        serviceData.put("totalServiceRevenue", totalServiceRevenue);


        return ResponseEntity.ok(serviceData);
    }

    @GetMapping("/api/users")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserData(
            @RequestParam(value = "start", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        if (start == null) {
            start = LocalDate.now().withDayOfYear(1);
        }
        if (end == null) {
            end = LocalDate.now();
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("newUsersStats", statsService.getNewUsersStats(start, end));
        userData.put("newUsersByMonth", statsService.getNewUsersByMonth(start, end));

        return ResponseEntity.ok(userData);
    }

}
