package com.team.hotelmanagementapp.pojo;

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
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "room")
@NamedQueries({
    @NamedQuery(name = "Room.findAll", query = "SELECT u FROM Room u"),
    @NamedQuery(name = "Room.findById", query = "SELECT u FROM Room u WHERE u.id = :id")})
public class Room implements Serializable {

    public enum Status {
        AVAILABLE("Phòng trống", "badge bg-success"),
        BOOKED("Đã đặt", "badge bg-warning"),
        OCCUPIED("Đang ở", "badge bg-primary"),
        CLEANING("Đang dọn", "badge bg-info"),
        MAINTENANCE("Bảo trì", "badge bg-danger");

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
    @Column(name = "room_number")
    private String roomNumber;

    @Basic(optional = false)
    @JoinColumn(name = "room_type_id")
    @ManyToOne
    private RoomType roomType;

    @Basic(optional = false)
    private Status status;

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

}
