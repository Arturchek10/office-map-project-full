package com.t1.map_service.model.entity;

import com.t1.map_service.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity  // говорим JPA, что это таблица в БД
@Table(name = "bookings") // имя таблицы
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Booking {
    @Id // первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY) // автоинкремент
    private Long id;

//    ID рабочего места (marker)
//    пока просто Long, без связи @ManyToOne
    @Column(name = "marker_id", nullable = false)
    private Long markerId;

//    ID пользвателя
    @Column(name = "user_id", nullable = false)
    private Long userId;

//    время начала брони
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

//    время окончания брони
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // статус брони (ACTIVE / CANCELLED / COMPLETED)
    @Enumerated(EnumType.STRING) // хранится как строка в БД
    @Column(nullable = false)
    private BookingStatus status;

    // когда создана запись
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // когда обновлялась
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // вызывается автоматически при создании
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        // если вдруг забыли задать статус — ставим ACTIVE
        if (this.status == null) {
            this.status = BookingStatus.ACTIVE;
        }
    }

    // вызывается при обновлении
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
