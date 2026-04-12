package com.t1.map_service.service.impl;

import com.t1.map_service.dto.layer.LayerCreateRequest;
import com.t1.map_service.dto.layer.LayerDto;
import com.t1.map_service.dto.layer.LayerUpdateRequest;
import com.t1.map_service.exception.EntityAlreadyExistsException;
import com.t1.map_service.mapper.LayerMapper;
import com.t1.map_service.model.entity.Floor;
import com.t1.map_service.model.entity.Layer;
import com.t1.map_service.repository.LayerRepository;
import com.t1.map_service.service.LayerService;
import com.t1.map_service.service.inner.FloorInnerService;
import com.t1.map_service.service.inner.LayerInnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LayerServiceImpl implements LayerService {

    private final LayerRepository repository;
    private final LayerMapper mapper;
    private final FloorInnerService floorInnerService;
    private final LayerInnerService layerInnerService;

    @Override
    @Transactional
    public LayerDto create(LayerCreateRequest request, Long floorId) {
        log.debug("Create layer: start (floorId={}, name={})", floorId, request.name());

        if (repository.existsByNameIgnoreCaseAndFloorId(request.name(), floorId)) {
            log.warn("Create layer: conflict — layer already exists (floorId={}, name={})",
                    floorId, request.name());
            throw new EntityAlreadyExistsException("Layer with name=%s already exists".formatted(request.name()));
        }

        Floor relatedFloor = floorInnerService.getEntityById(floorId);
        log.debug("Create layer: floor loaded (floorId={})", floorId);

        Layer layer = mapper.toEntity(request);
        layer.setFloor(relatedFloor);

        Layer saved = repository.save(layer);
        log.info("Layer created (layerId={}, floorId={}, name={})",
                saved.getId(), floorId, saved.getName());

        return mapper.toDto(saved);
    }

    @Override
    public LayerDto getById(Long layerId) {
        log.debug("Get layer by id: start (layerId={})", layerId);

        Layer layer = layerInnerService.getEntityById(layerId);
        log.debug("Get layer by id: success (layerId={}, floorId={}, name={})",
                layer.getId(),
                layer.getFloor().getId(),
                layer.getName());

        return mapper.toDto(layer);
    }

    @Override
    @Transactional
    public LayerDto update(LayerUpdateRequest request, Long layerId) {
        log.debug("Update layer: start (layerId={})", layerId);

        Layer layer = layerInnerService.getEntityById(layerId);
        mapper.update(layer, request);

        log.info("Layer updated (layerId={}, name={})", layerId, layer.getName());
        return mapper.toDto(layer);
    }

    @Override
    public void delete(Long layerId) {
        log.debug("Delete layer: start (layerId={})", layerId);

        Layer layer = layerInnerService.getEntityById(layerId);
        repository.delete(layer);

        log.info("Layer deleted (layerId={}, name={})", layerId, layer.getName());
    }
}
