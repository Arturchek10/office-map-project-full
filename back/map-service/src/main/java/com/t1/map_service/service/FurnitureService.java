package com.t1.map_service.service;

import com.t1.map_service.dto.furniture.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface FurnitureService {

    FurnitureDto getFurnitureById(Long furnitureId);

    Page<FurnitureShortDto> getCatalog(Pageable pageable);

    FurnitureDto create(FurnitureCreateRequest request, MultipartFile photo);

    FurnitureDto placeFurniture(Long floorId, FurniturePlaceRequest request);

    FurnitureDto move(Long furnitureId, FurnitureMoveRequest request);

    FurnitureDto updateUi(Long furnitureId, FurniturePatchUiRequest request);

    FurnitureDto update(Long furnitureId, FurniturePatchRequest request, MultipartFile photo);

    void deleteFurniture(Long furnitureId);


}
