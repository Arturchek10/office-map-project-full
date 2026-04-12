package com.t1.map_service.dto.marker;

import com.t1.map_service.model.Point;
import jakarta.validation.constraints.NotBlank;

public record CreateMarkerRequest(
        @NotBlank(message = "Type must exists")
        String type,
        Point position
) {
}
