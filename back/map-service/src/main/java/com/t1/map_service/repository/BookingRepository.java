package com.t1.map_service.repository;

import com.t1.map_service.model.entity.Booking;
import com.t1.map_service.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    /*
     * Проверяет, есть ли уже активная бронь на это место в выбранный промежуток времени.
     *
     * Логика пересечения:
     * существующая бронь конфликтует с новой, если:
     *
     * existing.startTime < newEndTime
     * И
     * existing.endTime > newStartTime
     *
     * Пример:
     * старая бронь: 10:00 - 12:00
     * новая бронь: 11:00 - 13:00
     *
     * 10:00 < 13:00 = true
     * 12:00 > 11:00 = true
     * значит есть пересечение.
     */
    boolean existsByMarkerIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Long markerId,
            BookingStatus status,
            LocalDateTime newEndTime,
            LocalDateTime newStartTime
    );

    /*
     * Получаем все активные брони конкретного маркера.
     * Это нужно, чтобы фронт мог понять:
     * место свободно или занято.
     */
    List<Booking> findByMarkerIdAndStatus(Long markerId, BookingStatus status);

    /*
     * Ищем активные брони среди списка markerId,
     * которые пересекаются с выбранным временем.
     */
    List<Booking> findByMarkerIdInAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            List<Long> markerIds,
            BookingStatus status,
            LocalDateTime endTime,
            LocalDateTime startTime
    );
}
