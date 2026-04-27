package com.t1.map_service.controller.marker;

import com.t1.map_service.dto.marker.CreateMarkerRequest;
import com.t1.map_service.dto.marker.MarkerDto;
import com.t1.map_service.dto.marker.MarkerMoveRequest;
import com.t1.map_service.dto.marker.UpdateMarkerRequest;
import com.t1.map_service.service.MarkerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/markers")
@RequiredArgsConstructor
public class MarkerController implements MarkerApi {

    private final MarkerService markerService;

    @Override
    @PostMapping("/{layerId}")
    @PreAuthorize("hasAuthority('ADMIN')")
//    @PreAuthorize("@perm.canManageLayer(authentication, #layerId)")
    public ResponseEntity<MarkerDto> createMarker(
            @PathVariable Long layerId,
            @RequestBody @Valid  CreateMarkerRequest dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(markerService.createMarker(layerId, dto));
    }

    @GetMapping("/all/{layerId}")
    public ResponseEntity<List<MarkerDto>> getMarker(
            @PathVariable Long layerId,
            @RequestParam(name = "hideUncomfortable", defaultValue = "false") boolean hideUncomfortable
    ) {
        return ResponseEntity.ok(markerService.getByLayerWithFilter(layerId, hideUncomfortable));
    }

    @Override
    @GetMapping("/{markerId}")
    public ResponseEntity<MarkerDto> getMarker(
            @PathVariable("markerId") Long markerId
    ) {
        return ResponseEntity.ok(markerService.getMarkerById(markerId));
    }

    @Override
    @PatchMapping("/{markerId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    //@PreAuthorize("@perm.canManageMarker(authentication, #markerId)")
    public ResponseEntity<MarkerDto> updateMarker(
            @RequestBody @Valid UpdateMarkerRequest dto,
            @PathVariable Long markerId
    ) {
        return ResponseEntity.ok(markerService.update(dto, markerId));
    }

    @Override
    @PatchMapping("/move/{markerId}")
    @PreAuthorize("hasAuthority('ADMIN')")
//    @PreAuthorize("@perm.canManageMarker(authentication, #markerId)")
    public ResponseEntity<MarkerDto> move(
            @PathVariable Long markerId,
            @RequestBody @Valid MarkerMoveRequest dto
    ) {
        return ResponseEntity.ok(markerService.moveMarker(dto, markerId));
    }

    @Override
    @DeleteMapping("/{markerId}")
    @PreAuthorize("hasAuthority('ADMIN')")
//    @PreAuthorize("@perm.canManageMarker(authentication, #markerId)")
    public ResponseEntity<Void> deleteMarker(
            @PathVariable Long markerId
    ) {
        markerService.delete(markerId);
        return ResponseEntity.noContent().build();
    }


}
