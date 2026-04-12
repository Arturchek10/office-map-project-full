package com.t1.map_service.dto.office;

import com.t1.map_service.dto.floor.FloorShortDto;

import java.util.List;

public record OfficeShortDto(
        Long id,
        String name,
        FloorShortDto startFloor,
        List<FloorShortDto> floors
) {
}
