package com.t1.map_service.model.entity;

import com.t1.map_service.service.photo.PhotoOwner;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "office")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Office implements PhotoOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String address;

    private String city;

    private Double latitude;
    private Double longitude;

    private String photoKey; // TODO S3 vault

    @OneToMany(mappedBy = "office", cascade = CascadeType.ALL)
    private List<Floor> floors;
}
