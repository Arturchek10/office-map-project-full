package com.t1.map_service.service.inner.inner_impl;

import com.t1.map_service.exception.EntityNotFoundException;
import com.t1.map_service.model.entity.Marker;
import com.t1.map_service.repository.MarkerRepository;
import com.t1.map_service.service.inner.MarkerInnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkerInnerServiceImpl implements MarkerInnerService {

    private final MarkerRepository repository;

    @Override
    public Marker getEntityById(Long markerId) {
        return repository.findById(markerId)
                .orElseThrow(() -> {
                    log.warn("Find marker: not found (markerId={})", markerId);
                    return new EntityNotFoundException("Marker with id=%d not found".formatted(markerId));
                });
    }

    @Override
    public List<Marker> getListByLayerIdWithFilter(Long layerId, boolean hideUncomfortable) {
        return repository.findByLayerWithUncomfortableFilter(layerId, hideUncomfortable);
    }
}
