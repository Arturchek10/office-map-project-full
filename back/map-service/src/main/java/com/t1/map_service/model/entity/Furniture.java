package com.t1.map_service.model.entity;

import com.t1.map_service.model.Point;
import com.t1.map_service.service.photo.PhotoOwner;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "furniture")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Furniture implements PhotoOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String photoKey;

    @Column(nullable = false)
    private Integer angle = 0;

    private Point position;

    @Column(nullable = false)
    private Short sizeFactor = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    private Floor floor;

    @PrePersist
    void initDefault() {
        if (angle == null) angle = 0;
        if (sizeFactor == null) sizeFactor = 1;
    }
}