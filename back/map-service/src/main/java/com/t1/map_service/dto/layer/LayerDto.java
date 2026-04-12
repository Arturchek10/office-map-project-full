package com.t1.map_service.dto.layer;

import com.t1.map_service.dto.marker.MarkerShortDto;

import java.util.List;

public record LayerDto(
    Long id,
    String name,
    List<MarkerShortDto> markers
) {
}
