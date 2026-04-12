package com.t1.map_service.mapper;

import com.t1.map_service.dto.layer.LayerCreateRequest;
import com.t1.map_service.dto.layer.LayerDto;
import com.t1.map_service.dto.layer.LayerUpdateRequest;
import com.t1.map_service.model.entity.Layer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = MarkerShortMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LayerMapper {

    LayerDto toDto(Layer layer);
    Layer toEntity(LayerCreateRequest request);

    void update(@MappingTarget Layer target, LayerUpdateRequest source);
}
