package com.team.hotelmanagementapp.dto;

import java.time.format.DateTimeFormatter;

import com.team.hotelmanagementapp.pojo.Booking;

/**
 * DTO for displaying booking information in templates
 * Avoids complex SpEL expressions by pre-formatting data
 */
public class BookingDisplayDTO {
    private final Booking booking;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public BookingDisplayDTO(Booking booking) {
        this.booking = booking;
    }
    
    // Original booking object
    public Booking getBooking() {
        return booking;
    }
    
    // Booking basic info
    public Integer getId() {
        return booking.getId();
    }
    
    public String getCode() {
        return booking.getCode();
    }
    
    public Booking.Status getStatus() {
        return booking.getStatus();
    }
    
    public String getStatusDescription() {
        return booking.getStatus() != null ? booking.getStatus().getDescription() : "N/A";
    }
    
    public String getStatusBadgeClass() {
        return booking.getStatus() != null ? booking.getStatus().getBadgeClass() : "badge bg-secondary";
    }
    
    // Customer info
    public String getCustomerName() {
        if (booking.getUser() != null) {
            String firstName = booking.getUser().getFirstName() != null ? booking.getUser().getFirstName() : "";
            String lastName = booking.getUser().getLastName() != null ? booking.getUser().getLastName() : "";
            return (firstName + " " + lastName).trim();
        }
        return "N/A";
    }
    
    public String getCustomerEmail() {
        return booking.getUser() != null ? booking.getUser().getEmail() : "";
    }
    
    public String getCustomerUsername() {
        return booking.getUser() != null ? booking.getUser().getUsername() : "";
    }
    
    // Room info
    public String getRoomName() {
        return booking.getRoom() != null ? booking.getRoom().getRoomNumber() : "N/A";
    }
    
    public String getRoomTypeName() {
        if (booking.getRoom() != null && booking.getRoom().getRoomType() != null) {
            return booking.getRoom().getRoomType().getName();
        }
        return "";
    }
    
    public Double getRoomPrice() {
        if (booking.getRoom() != null && booking.getRoom().getRoomType() != null) {
            return booking.getRoom().getRoomType().getPricePerNight();
        }
        return 0.0;
    }
    
    public String getRoomPriceFormatted() {
        Double price = getRoomPrice();
        return String.format("%,.0f", price) + "đ";
    }
    
    // Date formatting
    public String getCheckInDateFormatted() {
        return booking.getCheckInDate() != null ? booking.getCheckInDate().format(DATE_FORMATTER) : "N/A";
    }
    
    public String getCheckOutDateFormatted() {
        return booking.getCheckOutDate() != null ? booking.getCheckOutDate().format(DATE_FORMATTER) : "N/A";
    }
    
    public String getCreatedAtFormatted() {
        return booking.getCreatedAt() != null ? booking.getCreatedAt().format(DATETIME_FORMATTER) : "N/A";
    }
    
    public String getUpdatedAtFormatted() {
        return booking.getUpdatedAt() != null ? booking.getUpdatedAt().format(DATETIME_FORMATTER) : "N/A";
    }
    
    // Calculated fields
    public long getTotalNights() {
        if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        }
        return 0;
    }
    
    public String getTotalNightsText() {
        long nights = getTotalNights();
        return nights + " đêm";
    }
    
    public int getGuests() {
        return booking.getGuests() != null ? booking.getGuests() : 1;
    }
    
    public String getGuestsText() {
        return getGuests() + " khách";
    }
    
    public double getTotalAmount() {
        return getRoomPrice() * getTotalNights();
    }
    
    public String getTotalAmountFormatted() {
        return String.format("%,.0f", getTotalAmount()) + "đ";
    }
    
    // Special request
    public String getSpecialRequest() {
        return booking.getSpecialRequest() != null && !booking.getSpecialRequest().trim().isEmpty() 
            ? booking.getSpecialRequest() : null;
    }
    
    public boolean hasSpecialRequest() {
        return getSpecialRequest() != null;
    }
    
    // Status checks for UI logic
    public boolean canCancel() {
        return booking.getStatus() == Booking.Status.PENDING || 
               booking.getStatus() == Booking.Status.CONFIRMED;
    }
    
    public boolean canReview() {
        return booking.getStatus() == Booking.Status.CHECKED_OUT;
    }
    
    public boolean isActive() {
        return booking.getStatus() != Booking.Status.CANCELLED && 
               booking.getStatus() != Booking.Status.CHECKED_OUT;
    }
}