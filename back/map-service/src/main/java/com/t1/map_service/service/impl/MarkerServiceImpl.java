package com.t1.map_service.service.impl;

import com.t1.map_service.config.MarkerProperties;
import com.t1.map_service.dto.marker.CreateMarkerRequest;
import com.t1.map_service.dto.marker.UpdateMarkerRequest;
import com.t1.map_service.dto.marker.MarkerDto;
import com.t1.map_service.dto.marker.MarkerMoveRequest;
import com.t1.map_service.enums.MarkerType;
import com.t1.map_service.exception.UnsupportedStrategyTypeException;
import com.t1.map_service.mapper.MarkerMapper;
import com.t1.map_service.model.entity.Layer;
import com.t1.map_service.model.entity.Marker;
import com.t1.map_service.model.entity.description.Description;
import com.t1.map_service.repository.MarkerRepository;
import com.t1.map_service.service.MarkerService;
import com.t1.map_service.service.inner.LayerInnerService;
import com.t1.map_service.service.inner.MarkerInnerService;
import com.t1.map_service.strategy.description.DescriptionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkerServiceImpl implements MarkerService {

    private final MarkerMapper mapper;
    private final MarkerRepository repository;
    private final LayerInnerService layerInnerService;
    private final MarkerInnerService markerInnerService;
    private final Map<String, DescriptionStrategy> strategies;
    private final MarkerProperties markerProps;

    @Override
    @Transactional
    public MarkerDto createMarker(Long layerId, CreateMarkerRequest dto) {
        log.debug("Create marker: start (layerId={})", layerId);

        Layer layer = layerInnerService.getEntityById(layerId);
        log.debug("Create marker: layer loaded (layerId={})", layerId);

        MarkerType type;
        try{
            type = MarkerType.valueOf(dto.type().toUpperCase());
        } catch (IllegalArgumentException e){
            log.warn("Create marker: unsupported marker type (type={})", dto.type());
            throw new UnsupportedStrategyTypeException("Unsupported marker type=%s".formatted(dto.type()));
        }

        Marker marker = new Marker();
        marker.setLayer(layer);

        if (dto.position() != null) {
            marker.setPosition(dto.position());
        } else {
            marker.setPosition(markerProps.getDefaultPosition());
        }

        marker.setType(type);

        Marker saved = repository.save(marker);
        log.info("Marker created (markerId={}, layerId={}, type={})",
                saved.getId(), layerId, saved.getType());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public MarkerDto update(UpdateMarkerRequest dto, Long markerId){
        log.debug("Update marker: start (markerId={}, type={})", markerId, dto.type());

        Marker marker = markerInnerService.getEntityById(markerId);
        mapper.update(marker, dto);

        if (dto.payload() != null) {
            var strategy = strategies.get(dto.type().toLowerCase());
            if(strategy == null) {
                log.warn("Update marker: unsupported marker type (markerId={}, type={})", markerId, dto.type());
                throw new UnsupportedStrategyTypeException("Unsupported marker type=%s".formatted(dto.type()));
            }

            if(marker.getDescription() != null && strategy.supports().equals(marker.getType())) {
                strategy.patch(marker.getDescription(), dto.payload());
                log.debug("Update marker: description patched (markerId={}, type={})", markerId, dto.type());
            } else {
                marker.setDescription(strategy.build(dto.payload()));
                log.debug("Update marker: description built (markerId={}, type={})", markerId, dto.type());
            }
        }

        log.info("Marker updated (markerId={}, type={})", markerId, marker.getType());
        return mapper.toDto(marker);
    }

    @Override
    public void delete(Long markerId) {
        log.debug("Delete marker: start (markerId={})", markerId);

        Marker marker = markerInnerService.getEntityById(markerId);
        repository.delete(marker);

        log.info("Marker deleted (markerId={})", markerId);

    }

    @Override
    public List<MarkerDto> getByLayerWithFilter(Long layerId, boolean hideUncomfortable) {
        log.debug("Get markers: start (layerId={})", layerId);

        List<MarkerDto> markers = markerInnerService.getListByLayerIdWithFilter(layerId, hideUncomfortable).stream()
                .map(mapper::toDto)
                .toList();

        log.debug("Get markers: end (layerId={})", layerId);

        return markers;
    }

    @Override
    public MarkerDto getMarkerById(Long markerId) {
        log.debug("Get marker: start (markerId={})", markerId);

        Marker marker = markerInnerService.getEntityById(markerId);

        log.debug("Get marker: success (markerId={}, layerId={}, type={})",
                marker.getId(),
                marker.getLayer().getId(),
                marker.getType());

        return mapper.toDto(marker);
    }

    @Override
    @Transactional
    public MarkerDto moveMarker(MarkerMoveRequest request, Long markerId) {
        log.debug("Move marker: start (markerId={})", markerId);

        Marker marker = markerInnerService.getEntityById(markerId);
        marker.setPosition(request.position());

        log.debug("Marker moved (markerId={})", markerId);
        return mapper.toDto(marker);
    }


}
