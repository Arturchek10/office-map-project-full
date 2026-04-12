package com.t1.map_service.model.entity.description;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "utility_desc")
@Getter
@Setter
@NoArgsConstructor
public class UtilityDescription extends Description {
}
