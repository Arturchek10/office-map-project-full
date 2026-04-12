package com.t1.map_service.dto.furniture;

import com.t1.map_service.model.Point;
import jakarta.validation.constraints.NotNull;

public record FurnitureMoveRequest(
        @NotNull(message = "Position must exists")
        Point position
) {
}
