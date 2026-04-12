package com.t1.map_service.mapper;

import com.t1.map_service.dto.floor.FloorShortDto;
import com.t1.map_service.dto.office.OfficeShortDto;
import com.t1.map_service.model.entity.Floor;
import com.t1.map_service.model.entity.Office;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;

@Mapper(
        componentModel = "spring",
        uses = FloorShortMapper.class
)
public interface OfficeShortMapper {

    @Mapping(
            target = "startFloor", expression = "java(findStartFloor(office))"
    )
    OfficeShortDto toDto(Office office);

    default FloorShortDto findStartFloor(Office office) {
        return office.getFloors().stream()
                .filter(f -> f.getOrderNumber() >= 0)
                .min(Comparator.comparingInt(Floor::getOrderNumber))
                .map(f -> new FloorShortDto(f.getId(), f.getName(), f.getOrderNumber()))
                .orElse(null);
    }
}
