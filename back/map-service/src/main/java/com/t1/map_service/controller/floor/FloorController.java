package com.t1.map_service.controller.floor;

import com.t1.map_service.dto.floor.*;
import com.t1.map_service.service.FloorService;
import com.t1.map_service.service.FloorViewService;
import com.t1.map_service.service.facade.FloorFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/floors")
@RequiredArgsConstructor
public class FloorController implements FloorApi {

    private final FloorFacade floorFacade;
    private final FloorViewService floorViewService;
    private final FloorService floorService;

    @Override
    @GetMapping("/{floorId}")
    public ResponseEntity<FloorViewDto> getFloor(
            @PathVariable Long floorId
    ) {
        return ResponseEntity.ok(floorViewService.getFloorView(floorId));
    }

    @Override
    @PostMapping("/{officeId}")
    @PreAuthorize("@perm.canManageOffice(authentication, #officeId)")
    public ResponseEntity<FloorViewDto> create(
            @RequestBody @Valid FloorCreateRequest dto,
            @PathVariable Long officeId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(floorFacade.createFloorAndView(dto, officeId));
    }

    @Override
    @PatchMapping("/{floorId}")
    @PreAuthorize("@perm.canManageFloor(authentication, #floorId)")
    public ResponseEntity<FloorViewDto> update(
            @PathVariable Long floorId,
            @RequestBody @Valid FloorUpdateRequest dto
    ) {
        return ResponseEntity.ok(floorFacade.updateFloorAndView(floorId, dto));
    }

    @Override
    @PatchMapping(value = "/plan/{floorId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@perm.canManageFloor(authentication, #floorId)")
    public ResponseEntity<FloorViewDto> uploadPlan(
            @PathVariable Long floorId,
            @RequestPart("data") @Valid FloorPlanPatchRequest data,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) {
        if (Boolean.TRUE.equals(data.removePhoto()) && photo != null && !photo.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(floorFacade.updateFloorPlanAndView(floorId, data, photo));
    }

    @Override
    @DeleteMapping("/{floorId}")
    @PreAuthorize("@perm.canManageFloor(authentication, #floorId)")
    public ResponseEntity<Void> delete(
            @PathVariable Long floorId
    ) {
        floorService.delete(floorId);
        return ResponseEntity.noContent().build();
    }
}
