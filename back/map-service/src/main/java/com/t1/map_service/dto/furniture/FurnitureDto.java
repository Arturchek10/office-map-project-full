package com.t1.map_service.dto.furniture;

import com.t1.map_service.model.Point;

public record FurnitureDto(
        Long id,
        String name,
        String photoUrl,
        Integer angle,
        Point position,
        Short sizeFactor
) {
}
