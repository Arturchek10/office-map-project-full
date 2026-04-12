package com.t1.map_service.service;

import com.t1.map_service.dto.marker.CreateMarkerRequest;
import com.t1.map_service.dto.marker.UpdateMarkerRequest;
import com.t1.map_service.dto.marker.MarkerDto;
import com.t1.map_service.dto.marker.MarkerMoveRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MarkerService {

    MarkerDto createMarker(Long layerId, CreateMarkerRequest dto);

    MarkerDto getMarkerById(Long markerId);

    MarkerDto moveMarker(MarkerMoveRequest request, Long markerId);

    MarkerDto update(UpdateMarkerRequest dto, Long markerId);

    void delete(Long markerId);

    List<MarkerDto> getByLayerWithFilter(Long layerId, boolean hideUncomfortable);
}
