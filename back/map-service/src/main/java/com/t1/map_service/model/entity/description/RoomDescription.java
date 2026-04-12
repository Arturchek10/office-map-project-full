package com.t1.map_service.model.entity.description;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "room_desc")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoomDescription extends Description {

    private Integer capacity;

}
