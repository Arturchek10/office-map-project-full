package com.t1.map_service.dto.floor;

import com.t1.map_service.dto.furniture.FurnitureDto;
import com.t1.map_service.dto.layer.BaseLayerDto;
import com.t1.map_service.dto.layer.LayerShortDto;

import java.util.List;

public record FloorViewDto(
        Long id,
        String name,
        Integer orderNumber,
        String photoUrl,
        List<LayerShortDto> layers,
        BaseLayerDto baseLayer,
        List<FurnitureDto> furnitures
) {
}
