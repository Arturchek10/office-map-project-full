package com.t1.map_service.model.entity;

import com.t1.map_service.enums.MarkerType;
import com.t1.map_service.model.Point;
import com.t1.map_service.model.entity.description.Description;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "markers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Marker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private MarkerType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "layer_id")
    private Layer layer;

    private Point position;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "description_id")
    private Description description;

    @Column(name = "is_uncomfortable")
    private boolean uncomfortable;
}
