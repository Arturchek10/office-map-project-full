package com.t1.map_service.dto.booking;

import java.time.LocalDateTime;

public record CreateBookingRequest(
        Long markerId,              // какое место бронируем
        LocalDateTime startTime,    // начало бронирования
        LocalDateTime endTime       // конец бронирования
) {
}