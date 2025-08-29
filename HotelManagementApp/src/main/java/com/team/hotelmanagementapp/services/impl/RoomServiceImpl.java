package com.team.hotelmanagementapp.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.hotelmanagementapp.pojo.Room;
import com.team.hotelmanagementapp.repositories.BookingRepository;
import com.team.hotelmanagementapp.repositories.RoomRepository;
import com.team.hotelmanagementapp.services.RoomService;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Room getById(int id) {
        return roomRepository.getById(id);
    }

    @Override
    public List<Room> find(Map<String, String> params, Boolean available) {
        return roomRepository.find(params, available);
    }

    @Override
    public Room createOrUpdate(Room room) {
        return roomRepository.createOrUpdate(room);
    }

    @Override
    public void delete(int id) {
        roomRepository.delete(id);
    }

    @Override
    public void delete(List<Integer> ids) {
        roomRepository.delete(ids);
    }

    @Override
    public long countRooms(Map<String, String> params, Boolean available) {
        return roomRepository.countRooms(params, available);
    }

    @Override
    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        List<Room> allRooms = roomRepository.findAll();
        List<Room> availableRooms = new ArrayList<>();

        for (Room room : allRooms) {
            if (room.getStatus() == Room.Status.AVAILABLE &&
                !bookingRepository.isRoomBooked(room.getId(), checkIn, checkOut)) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

    @Override
    public List<Room> findAvailableRoomsByType(int roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        Map<String, String> params = new HashMap<>();
        params.put("roomTypeId", String.valueOf(roomTypeId));
        List<Room> roomsOfType = roomRepository.find(params, true);

        List<Room> availableRooms = new ArrayList<>();
        for (Room room : roomsOfType) {
            if (room.getStatus() == Room.Status.AVAILABLE &&
                !bookingRepository.isRoomBooked(room.getId(), checkIn, checkOut)) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) {
        Room room = roomRepository.getById(roomId);
        if (room == null || room.getStatus() != Room.Status.AVAILABLE) {
            return false;
        }

        return !bookingRepository.isRoomBooked(roomId, checkIn, checkOut);
    }

    @Override
    public List<Room> findAvailableRoomsWithFilters(Map<String, Object> filters) {
        // Extract date range from filters
        LocalDate checkIn = filters.containsKey("checkIn") ?
            LocalDate.parse(filters.get("checkIn").toString()) : null;
        LocalDate checkOut = filters.containsKey("checkOut") ?
            LocalDate.parse(filters.get("checkOut").toString()) : null;

        // If no date range specified, return all available rooms
        if (checkIn == null || checkOut == null) {
            Map<String, String> params = new HashMap<>();
            params.put("status", Room.Status.AVAILABLE.toString());
            return roomRepository.find(params, true);
        }

        // Build availability query
        List<Room> candidates;

        // Filter by room type if specified
        if (filters.containsKey("roomTypeId")) {
            String roomTypeId = filters.get("roomTypeId").toString();
            candidates = findAvailableRoomsByType(Integer.parseInt(roomTypeId), checkIn, checkOut);
        } else {
            candidates = findAvailableRooms(checkIn, checkOut);
        }

        // Apply additional filters (maxGuests, price range, etc.)
        if (filters.containsKey("maxGuests")) {
            int maxGuests = Integer.parseInt(filters.get("maxGuests").toString());
            candidates = candidates.stream()
                .filter(room -> room.getRoomType() != null &&
                               room.getRoomType().getMaxGuests() >= maxGuests)
                .collect(java.util.stream.Collectors.toList());
        }

        if (filters.containsKey("minPrice")) {
            double minPrice = Double.parseDouble(filters.get("minPrice").toString());
            candidates = candidates.stream()
                .filter(room -> room.getRoomType() != null &&
                               room.getRoomType().getPricePerNight() >= minPrice)
                .collect(java.util.stream.Collectors.toList());
        }

        if (filters.containsKey("maxPrice")) {
            double maxPrice = Double.parseDouble(filters.get("maxPrice").toString());
            candidates = candidates.stream()
                .filter(room -> room.getRoomType() != null &&
                               room.getRoomType().getPricePerNight() <= maxPrice)
                .collect(java.util.stream.Collectors.toList());
        }

        return candidates;
    }
}