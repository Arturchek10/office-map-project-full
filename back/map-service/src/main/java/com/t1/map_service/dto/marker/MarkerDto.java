package com.t1.map_service.dto.marker;

import com.t1.map_service.dto.description.DescriptionDto;
import com.t1.map_service.model.Point;

public record MarkerDto(
        Long id,
        String name,
        String type,
        Point position,
        boolean uncomfortable,
        DescriptionDto payload
) {
}
