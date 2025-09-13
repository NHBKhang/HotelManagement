package com.team.hotelmanagementapp.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "room")
@NamedQueries({
    @NamedQuery(name = "Room.findAll", query = "SELECT u FROM Room u"),
    @NamedQuery(name = "Room.findById", query = "SELECT u FROM Room u WHERE u.id = :id")})
public class Room implements Serializable {

    public enum Status {
        AVAILABLE("Phòng trống", "badge bg-success", "bg-green-100 text-green-800"),
        BOOKED("Đã đặt", "badge bg-warning", "bg-yellow-100 text-yellow-800"),
        OCCUPIED("Đang ở", "badge bg-primary", "bg-blue-100 text-blue-800"),
        CLEANING("Đang dọn", "badge bg-info", "bg-sky-100 text-sky-800"),
        MAINTENANCE("Bảo trì", "badge bg-danger", "bg-red-100 text-red-800");

        private final String description;
        private final String badgeClass;
        private final String tailwindClass;

        Status(String description, String badgeClass, String tailwindClass) {
            this.description = description;
            this.badgeClass = badgeClass;
            this.tailwindClass = tailwindClass;
        }

        public String getDescription() {
            return description;
        }

        public String getBadgeClass() {
            return badgeClass;
        }

        public String getTailwindClass() {
            return tailwindClass;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;

    @Basic(optional = false)
    @Column(name = "code", nullable = false)
    private String code;

    @Basic(optional = false)
    @Column(name = "room_number", unique = true)
    private String roomNumber;

    @Basic(optional = false)
    @JoinColumn(name = "room_type_id")
    @ManyToOne
    private RoomType roomType;

    @Basic(optional = false)
    private Status status;
    
    private String image;
    
    private Double size;

    @Transient
    private MultipartFile file;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "room")
    @JsonIgnore
    private List<Booking> bookings;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.roomNumber);
        hash = 19 * hash + Objects.hashCode(this.roomType);
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
        final Room other = (Room) obj;
        if (!Objects.equals(this.roomNumber, other.roomNumber)) {
            return false;
        }
        return Objects.equals(this.roomType, other.roomType);
    }

    @Override
    public String toString() {
        return "Room{" + "roomNumber=" + roomNumber + '}';
    }

    public Room(Integer id, String roomNumber, RoomType roomType, Status status) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.status = status;
    }

    public Room() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    @Transient
    public String getStatusDescription() {
        return status != null ? status.getDescription() : "Không xác định";
    }
    
    @Transient
    public String getTailwindClass() {
        return status != null ? status.getTailwindClass(): "";
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
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

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

}
