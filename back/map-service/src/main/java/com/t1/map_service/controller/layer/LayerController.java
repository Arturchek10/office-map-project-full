package com.t1.map_service.controller.layer;

import com.t1.map_service.dto.layer.LayerCreateRequest;
import com.t1.map_service.dto.layer.LayerDto;
import com.t1.map_service.dto.layer.LayerUpdateRequest;
import com.t1.map_service.service.LayerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/layers")
@RequiredArgsConstructor
public class LayerController implements LayerApi {

    private final LayerService layerService;

    @Override
    @PostMapping("/{floorId}")
    @PreAuthorize("@perm.canManageFloor(authentication, #floorId)")
    public ResponseEntity<LayerDto> create(
            @PathVariable Long floorId,
            @RequestBody @Valid  LayerCreateRequest dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(layerService.create(dto, floorId));
    }

    @Override
    @GetMapping("/{layerId}")
    public ResponseEntity<LayerDto> getById(
            @PathVariable Long layerId
    ) {
        return ResponseEntity.ok(layerService.getById(layerId));
    }

    @Override
    @PatchMapping("/{layerId}")
    @PreAuthorize("@perm.canManageLayer(authentication, #layerId)")
    public ResponseEntity<LayerDto> update(
            @RequestBody @Valid  LayerUpdateRequest dto,
            @PathVariable Long layerId
    ) {
        return ResponseEntity.ok(layerService.update(dto, layerId));
    }

    @Override
    @DeleteMapping("/{layerId}")
    @PreAuthorize("@perm.canManageLayer(authentication, #layerId)")
    public ResponseEntity<Void> delete(
            @PathVariable Long layerId
    ) {
        layerService.delete(layerId);
        return ResponseEntity.noContent().build();
    }

}
