package com.t1.map_service.mapper;

import com.t1.map_service.dto.layer.LayerShortDto;
import com.t1.map_service.model.entity.Layer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LayerShortMapper {

    LayerShortDto toDto(Layer layer);
    List<LayerShortDto> toDtoList(List<Layer> layers);
}
