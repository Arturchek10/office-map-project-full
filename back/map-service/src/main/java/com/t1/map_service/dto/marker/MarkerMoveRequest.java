package com.t1.map_service.dto.marker;

import com.t1.map_service.model.Point;
import jakarta.validation.constraints.NotNull;

public record MarkerMoveRequest(
        @NotNull(message = "Position must exists")
        Point position
) {
}
