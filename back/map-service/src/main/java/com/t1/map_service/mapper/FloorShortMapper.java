package com.t1.map_service.mapper;

import com.t1.map_service.dto.floor.FloorShortDto;
import com.t1.map_service.model.entity.Floor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FloorShortMapper {

    FloorShortDto toDto(Floor floor);
    Floor toEntity(FloorShortDto floorShortDto);
}
