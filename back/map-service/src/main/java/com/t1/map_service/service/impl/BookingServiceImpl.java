package com.t1.map_service.service.impl;

import com.t1.map_service.dto.booking.CreateBookingRequest;
import com.t1.map_service.enums.BookingStatus;
import com.t1.map_service.exception.BookingConflictException;
import com.t1.map_service.model.entity.Booking;
import com.t1.map_service.repository.BookingRepository;
import com.t1.map_service.repository.MarkerRepository;
import com.t1.map_service.service.BookingService;
import org.springframework.stereotype.Service;
import com.t1.map_service.model.entity.Marker;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final MarkerRepository markerRepository;

    // Spring передаст сюда оба repository
    public BookingServiceImpl(
            BookingRepository bookingRepository,
            MarkerRepository markerRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.markerRepository = markerRepository;
    }

    @Override
    public Booking createBooking(CreateBookingRequest request, Long userId) {
        validateBookingTime(request);

        boolean hasConflict =
                bookingRepository.existsByMarkerIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        request.markerId(),
                        BookingStatus.ACTIVE,
                        request.endTime(),
                        request.startTime()
                );

        if (hasConflict) {
            throw new BookingConflictException("Место уже забронировано на выбранное время");
        }

        Booking booking = Booking.builder()
                .markerId(request.markerId())
                .userId(userId)
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(BookingStatus.ACTIVE)
                .build();

        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getActiveBookingsByMarkerId(Long markerId) {
        return bookingRepository.findByMarkerIdAndStatus(
                markerId,
                BookingStatus.ACTIVE
        );
    }

    private void validateBookingTime(CreateBookingRequest request) {
        if (request.markerId() == null) {
            throw new RuntimeException("Не указано рабочее место");
        }

        if (request.startTime() == null || request.endTime() == null) {
            throw new RuntimeException("Не указано время бронирования");
        }

        if (!request.startTime().isBefore(request.endTime())) {
            throw new RuntimeException("Время начала должно быть раньше времени окончания");
        }
    }

    @Override
    public List<Booking> getActiveBookingsByFloorId(
            Long floorId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        if (floorId == null) {
            throw new RuntimeException("Не указан этаж");
        }

        if (startTime == null || endTime == null) {
            throw new RuntimeException("Не указано время");
        }

        if (!startTime.isBefore(endTime)) {
            throw new RuntimeException("Время начала должно быть раньше конца");
        }

        // 1. получаем все маркеры этажа
        List<Marker> markers = markerRepository.findByLayer_Floor_Id(floorId);

        // 2. берём только их id
        List<Long> markerIds = markers.stream()
                .map(Marker::getId)
                .toList();

        if (markerIds.isEmpty()) {
            return List.of();
        }

        // 3. ищем брони по этим маркерам
        return bookingRepository.findByMarkerIdInAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                markerIds,
                BookingStatus.ACTIVE,
                endTime,
                startTime
        );
    }
}