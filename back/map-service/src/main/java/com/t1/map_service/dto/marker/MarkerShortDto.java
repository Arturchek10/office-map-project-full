package com.t1.map_service.dto.marker;

import com.t1.map_service.model.Point;

public record MarkerShortDto(
        Long id,
        Point position,
        String type
) {
}
