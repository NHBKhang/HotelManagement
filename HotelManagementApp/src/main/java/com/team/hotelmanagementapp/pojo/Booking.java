package com.team.hotelmanagementapp.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "booking")
@JsonIgnoreProperties(value = {"feedbacks"}, allowSetters = true)
@NamedQueries({
    @NamedQuery(name = "Booking.findAll", query = "SELECT u FROM Booking u"),
    @NamedQuery(name = "Booking.findById", query = "SELECT u FROM Booking u WHERE u.id = :id")})
public class Booking implements Serializable {

    public enum Status {
        PENDING("Chờ xác nhận", "badge bg-secondary"),
        PROCESSING("Chờ xử lý", "badge bg-warning"),
        CONFIRMED("Đã xác nhận", "badge bg-primary"),
        CHECKED_IN("Đang ở", "badge bg-success"),
        CHECKED_OUT("Đã trả phòng", "badge bg-info"),
        CANCELLED("Đã hủy", "badge bg-danger");

        private final String description;
        private final String badgeClass;

        Status(String description, String badgeClass) {
            this.description = description;
            this.badgeClass = badgeClass;
        }

        public String getDescription() {
            return description;
        }

        public String getBadgeClass() {
            return badgeClass;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;

    @Basic(optional = false)
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Basic(optional = false)
    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Basic(optional = false)
    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    private Integer guests;

    @Basic(optional = false)
    private Status status;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Basic(optional = true)
    @Column(name = "special_request")
    private String specialRequest;

    @OneToMany(mappedBy = "booking", orphanRemoval = true)
    private List<Feedback> feedbacks;

    public Booking() {
    }

    public Booking(Integer id, User user, Room room, LocalDate checkInDate, LocalDate checkOutDate, 
            Status status, String specialRequest, int guests) {
        this.id = id;
        this.user = user;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.specialRequest = specialRequest;
        this.guests = guests;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSpecialRequest() {
        return specialRequest;
    }

    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.user);
        hash = 97 * hash + Objects.hashCode(this.room);
        hash = 97 * hash + Objects.hashCode(this.checkInDate);
        hash = 97 * hash + Objects.hashCode(this.checkOutDate);
        hash = 97 * hash + Objects.hashCode(this.status);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Booking other = (Booking) obj;
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.room, other.room)) {
            return false;
        }
        if (!Objects.equals(this.checkInDate, other.checkInDate)) {
            return false;
        }
        return Objects.equals(this.checkOutDate, other.checkOutDate);
    }

    @Override
    public String toString() {
        return "Booking{" + "user=" + user + ", room=" + room + ", checkInDate=" + checkInDate + ", checkOutDate=" + checkOutDate + '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getGuests() {
        return guests;
    }

    public void setGuests(Integer guests) {
        this.guests = guests;
    }

}
