package com.t1.map_service.service;

import com.t1.map_service.dto.layer.LayerCreateRequest;
import com.t1.map_service.dto.layer.LayerDto;
import com.t1.map_service.dto.layer.LayerUpdateRequest;
import com.t1.map_service.model.entity.Layer;

public interface LayerService {

    LayerDto create(LayerCreateRequest request, Long floorId);

    LayerDto getById(Long id);

    LayerDto update(LayerUpdateRequest request, Long layerId);

    void delete(Long layerId);
}
