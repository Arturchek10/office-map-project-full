package com.t1.map_service.controller.booking;

import com.t1.map_service.dto.booking.CreateBookingRequest;
import com.t1.map_service.model.entity.Booking;
import com.t1.map_service.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController // говорит Spring что это REST API
@RequestMapping("/api/v1/bookings") // базовый путь
public class BookingController {

    private final BookingService bookingService;

    // внедрение зависимости
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

//   POST /api/v1/bookings
//     Создание бронирования
    @PostMapping
    public Booking createBooking(
            @RequestBody CreateBookingRequest request // JSON с фронта
    ) {

        // пока захардкодим userId
        // потом заменим на JWT
        Long userId = 1L;

        return bookingService.createBooking(request, userId);
    }


//     GET /api/v1/bookings/marker/{markerId}
//      Возвращает активные бронирования одного рабочего места.
    @GetMapping("/marker/{markerId}")
    public List<Booking> getBookingsByMarker(
            @PathVariable Long markerId
    ) {
        return bookingService.getActiveBookingsByMarkerId(markerId);
    }

// возвращается все брони с этажа
    @GetMapping("/floor/{floorId}")
    public List<Booking> getBookingsByFloor(
            @PathVariable Long floorId,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime
    ) {
        return bookingService.getActiveBookingsByFloorId(floorId, startTime, endTime);
    }
}