package com.t1.map_service.model.entity;

import com.t1.map_service.service.photo.PhotoOwner;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "floors")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Floor implements PhotoOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int orderNumber;

    private String photoKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL)
    private List<Layer> layers;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL)
    private List<Furniture> furnitures;
}
