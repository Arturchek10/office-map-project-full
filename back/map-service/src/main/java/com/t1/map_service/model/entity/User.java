package com.t1.map_service.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    // Здесь хранится уже захешированный пароль, а не обычный текст
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;
}