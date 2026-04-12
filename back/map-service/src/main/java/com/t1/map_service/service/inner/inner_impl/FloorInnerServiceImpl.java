package com.t1.map_service.service.inner.inner_impl;

import com.t1.map_service.exception.EntityNotFoundException;
import com.t1.map_service.model.entity.Floor;
import com.t1.map_service.repository.FloorRepository;
import com.t1.map_service.service.inner.FloorInnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FloorInnerServiceImpl implements FloorInnerService {

    private final FloorRepository repository;

    @Override
    public Floor getEntityById(Long floorId) {
        return repository.findById(floorId)
                .orElseThrow(() -> {
                    log.warn("Find floor: not found (floorId={})", floorId);
                    return new EntityNotFoundException("Floor with id=%d not found".formatted(floorId));
                });
    }
}
