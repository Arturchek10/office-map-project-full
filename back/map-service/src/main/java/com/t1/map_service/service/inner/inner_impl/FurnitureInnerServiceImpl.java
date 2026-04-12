package com.t1.map_service.service.inner.inner_impl;

import com.t1.map_service.exception.EntityNotFoundException;
import com.t1.map_service.model.entity.Furniture;
import com.t1.map_service.repository.FurnitureRepository;
import com.t1.map_service.service.inner.FurnitureInnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class FurnitureInnerServiceImpl implements FurnitureInnerService {

    private final FurnitureRepository repository;

    @Override
    public Furniture getEntityById(Long furnitureId) {
        return repository.findById(furnitureId)
                .orElseThrow(() ->{
                    log.warn("Find furniture: not found (furnitureId={})", furnitureId);
                    return new EntityNotFoundException("Furniture with id=%d not found".formatted(furnitureId));
                });
    }

    @Override
    public Page<Furniture> getAllUnique(Pageable pageable) {
        return repository.findAllUnique(pageable);
    }
}
