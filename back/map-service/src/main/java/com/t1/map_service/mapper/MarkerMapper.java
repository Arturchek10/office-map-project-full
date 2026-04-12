package com.t1.map_service.mapper;

import com.t1.map_service.dto.marker.UpdateMarkerRequest;
import com.t1.map_service.dto.marker.MarkerDto;
import com.t1.map_service.enums.MarkerType;
import com.t1.map_service.model.entity.Marker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = DescriptionMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MarkerMapper {

    @Mapping(target = "description", ignore = true)
    @Mapping(target = "type", expression = "java(mapTypeFromRequest(source))")
    void update(@MappingTarget Marker target, UpdateMarkerRequest source);

    @Mapping(source = "description", target = "payload")
    @Mapping(target = "type", expression = "java(mapTypeToString(marker))")
    MarkerDto toDto(Marker marker);

    List<MarkerDto> toDtoList(List<Marker> markers);

    Marker toEntity(UpdateMarkerRequest request);

    // Маппинг из строки в енам
    default MarkerType mapTypeFromRequest(UpdateMarkerRequest source) {
        return MarkerType.valueOf(source.type().toUpperCase());
    }

    // Маппинг из enum в строку
    default String mapTypeToString(Marker marker) {
        return marker.getType().getValue();
    }
}
