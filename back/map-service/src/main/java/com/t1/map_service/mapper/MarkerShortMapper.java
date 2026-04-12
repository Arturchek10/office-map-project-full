package com.t1.map_service.mapper;

import com.t1.map_service.dto.marker.MarkerShortDto;
import com.t1.map_service.enums.MarkerType;
import com.t1.map_service.model.entity.Marker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MarkerShortMapper {

    @Mapping(
            target = "type",
            expression = "java(mapType(marker))"
    )
    MarkerShortDto toDto(Marker marker);

    List<MarkerShortDto> toDtoList(List<Marker> markers);

    default String mapType(Marker marker) {
        return marker.getType().getValue();
    }
}
