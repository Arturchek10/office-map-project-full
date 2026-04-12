package com.t1.map_service.service.impl;

import com.t1.map_service.dto.floor.FloorViewDto;
import com.t1.map_service.dto.furniture.FurnitureDto;
import com.t1.map_service.dto.layer.BaseLayerDto;
import com.t1.map_service.dto.layer.LayerShortDto;
import com.t1.map_service.exception.EntityNotFoundException;
import com.t1.map_service.mapper.LayerShortMapper;
import com.t1.map_service.mapper.MarkerShortMapper;
import com.t1.map_service.mapper.furniture.FurnitureMapper;
import com.t1.map_service.model.entity.Floor;
import com.t1.map_service.model.entity.Furniture;
import com.t1.map_service.model.entity.Layer;
import com.t1.map_service.model.entity.Marker;
import com.t1.map_service.repository.FloorRepository;
import com.t1.map_service.repository.LayerRepository;
import com.t1.map_service.repository.MarkerRepository;
import com.t1.map_service.service.FloorViewService;
import com.t1.map_service.service.photo.impl.FloorPhotoService;
import com.t1.map_service.storage.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;


// Сервис для отдачи базового слоя этажа
@Service
@RequiredArgsConstructor
@Slf4j
public class FloorViewServiceImpl implements FloorViewService {

    private final FloorRepository floorRepository;
    private final LayerRepository layerRepository;
    private final MarkerRepository markerRepository;
    private final LayerShortMapper layerShortMapper;
    private final MarkerShortMapper markerShortMapper;
    private final FurnitureMapper furnitureMapper;

    private final FloorPhotoService photoService;
    private final FileStorageService storage;

    @Value("${app.files.presign_minutes:10}")
    private int presignMinutes;

    private Duration durationTtl;

    @PostConstruct
    public void init() {
        this.durationTtl = Duration.ofMinutes(presignMinutes);
    }

    @Override
    public FloorViewDto getFloorView(Long floorId) {
        log.debug("Get floor view: start (floorId={})", floorId);

        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> {
                    log.warn("Get floor view: floor not found (floorId={})", floorId);
                    return new EntityNotFoundException("Floor with id=%d not found".formatted(floorId));
                });

        // Все слои этажа
        List<Layer> layers = layerRepository.findByFloorIdOrderByNameAsc(floorId);
        log.debug("Get floor view: layers loaded (floorId={}, layers={})",
                floorId, layers.size());

        // Базовый слой
        Layer baseLayer = layerRepository.findByFloorIdAndBaseTrue(floorId)
                .orElseThrow(() -> {
                    log.warn("Get floor view: base layer not found (floorId={})", floorId);
                    return new EntityNotFoundException("Base Layer for floor with id=%d not found".formatted(floorId));
                });

        // Все маркеры этажа
        List<Marker> allMarkers = markerRepository.findByLayer_Floor_Id(floorId);
        log.debug("Get floor view: markers loaded (floorId={}, markers={})",
                floorId, allMarkers.size());

        // Базовый слой со всеми маркерами
        BaseLayerDto baseLayerDto = new BaseLayerDto(
                baseLayer.getId(),
                baseLayer.getName(),
                true,
                markerShortMapper.toDtoList(allMarkers)
        );

        // Слои для переключения
        List<LayerShortDto> layersShort = layerShortMapper.toDtoList(layers);

        // Список мебели для этажа
        List<FurnitureDto> furnitures = Collections.emptyList();

        List<Furniture> listFurnitures = floor.getFurnitures();

        if(listFurnitures != null) {
            furnitures = listFurnitures.stream()
                    .map(f -> furnitureMapper.toDto(f, storage, durationTtl))
                    .toList();
        }

        String photoUrl = photoService.presign(floor, durationTtl);

        FloorViewDto result = new FloorViewDto(
                floor.getId(),
                floor.getName(),
                floor.getOrderNumber(),
                photoUrl,
                layersShort,
                baseLayerDto,
                furnitures
        );

        log.debug("Get floor view: success (floorId={}, baseLayerId={}, switchableLayers={})",
                floorId, baseLayer.getId(), layersShort.size());

        return result;
    }
}
