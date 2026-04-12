package com.t1.map_service.model.entity.description;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "emergency_desc")
@Getter
@Setter
@NoArgsConstructor
public class EmergencyDescription extends Description {
}
