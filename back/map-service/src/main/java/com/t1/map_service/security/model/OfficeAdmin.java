package com.t1.map_service.security.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "office_admins", uniqueConstraints = @UniqueConstraint(columnNames = {"office_id","login"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficeAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long officeId;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
