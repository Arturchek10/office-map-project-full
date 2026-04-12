package com.t1.map_service.dto.furniture;

import com.t1.map_service.model.Point;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record FurniturePlaceRequest(
        @NotBlank(message = "Name must exist")
        String name,
        @NotNull(message = "Position must exists")
        Point position,
        @NotBlank(message = "Photo url must exists")
        String photoUrl
) {
}
