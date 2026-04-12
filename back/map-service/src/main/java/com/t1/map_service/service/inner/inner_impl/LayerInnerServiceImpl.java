package com.t1.map_service.service.inner.inner_impl;

import com.t1.map_service.exception.EntityAlreadyExistsException;
import com.t1.map_service.exception.EntityNotFoundException;
import com.t1.map_service.model.entity.Floor;
import com.t1.map_service.model.entity.Layer;
import com.t1.map_service.repository.LayerRepository;
import com.t1.map_service.service.inner.FloorInnerService;
import com.t1.map_service.service.inner.LayerInnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LayerInnerServiceImpl implements LayerInnerService {

    private final static String BASIC_LAYER_NAME = "Базовый слой";

    private final FloorInnerService floorInnerService;
    private final LayerRepository repository;

    @Override
    public Layer getEntityById(Long layerId) {
        return repository.findById(layerId)
                .orElseThrow(() ->{
                    log.warn("Find layer: not found (layerId={})", layerId);
                    return new EntityNotFoundException("Layer with id=%d not found".formatted(layerId));
                });
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void createBaseLayer(Long floorId) {
        log.debug("Create base layer: start (floorId={}, baseName={})",
                floorId, BASIC_LAYER_NAME);

        Floor relatedFloor = floorInnerService.getEntityById(floorId);
        if(repository.existsByFloorIdAndBaseTrue(floorId)){
            log.warn("Create base layer: conflict — base layer already exists (floorId={})", floorId);
            throw new EntityAlreadyExistsException("Base layer for floor with id=%d already exists".formatted(floorId));
        }

        Layer baseLayer = Layer.builder()
                .name(BASIC_LAYER_NAME)
                .base(true)
                .floor(relatedFloor)
                .build();

        Layer saved = repository.save(baseLayer);
        log.info("Base layer created (layerId={}, floorId={}, name={})",
                saved.getId(), floorId, saved.getName());
    }
}
