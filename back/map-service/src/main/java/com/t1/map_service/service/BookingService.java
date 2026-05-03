package com.t1.map_service.service;

import com.t1.map_service.dto.booking.CreateBookingRequest;
import com.t1.map_service.model.entity.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface    BookingService {

    Booking createBooking(CreateBookingRequest request, Long userId);

    List<Booking> getActiveBookingsByMarkerId(Long markerId);

    List<Booking> getActiveBookingsByFloorId(
            Long floorId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );
}