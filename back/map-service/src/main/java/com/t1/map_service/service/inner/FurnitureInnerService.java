package com.t1.map_service.service.inner;

import com.t1.map_service.model.entity.Furniture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FurnitureInnerService {

    Furniture getEntityById(Long furnitureId);

    Page<Furniture> getAllUnique(Pageable pageable);
}
