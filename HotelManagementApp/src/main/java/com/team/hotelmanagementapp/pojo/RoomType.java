package com.team.hotelmanagementapp.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "room_type")
@NamedQueries({
    @NamedQuery(name = "RoomType.findAll", query = "SELECT u FROM RoomType u"),
    @NamedQuery(name = "RoomType.findById", query = "SELECT u FROM RoomType u WHERE u.id = :id")})
public class RoomType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;

    @Basic(optional = false)
    private String name;

    @Basic(optional = true)
    private String description;

    @Basic(optional = false)
    @Column(name = "price_per_night")
    private Double pricePerNight;

    @Basic(optional = false)
    @Column(name = "max_guests")
    private Integer maxGuests;

//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "room_amenities", joinColumns = @JoinColumn(name = "room_id"))
//    @Column(name = "amenity")
//    private List<String> amenities;

    private String policy;

    public RoomType() {
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.name);
        hash = 37 * hash + Objects.hashCode(this.pricePerNight);
        hash = 37 * hash + Objects.hashCode(this.maxGuests);
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
        final RoomType other = (RoomType) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.pricePerNight, other.pricePerNight)) {
            return false;
        }
        return Objects.equals(this.maxGuests, other.maxGuests);
    }

    @Override
    public String toString() {
        return "RoomType{" + "name=" + name + '}';
    }

    public RoomType(Integer id, String name, String description, Double pricePerNight, Integer maxGuests) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.maxGuests = maxGuests;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public Integer getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(Integer maxGuests) {
        this.maxGuests = maxGuests;
    }

//    public List<String> getAmenities() {
//        return amenities;
//    }

//    public void setAmenities(List<String> amenities) {
//        this.amenities = amenities;
//    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }
    
}
