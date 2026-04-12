package com.t1.map_service.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "layers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Layer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean base;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    @OneToMany(mappedBy = "layer", cascade = CascadeType.ALL)
    private List<Marker> markers;
}
