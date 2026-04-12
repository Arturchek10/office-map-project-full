package com.t1.map_service.controller.office;

import com.t1.map_service.dto.office.OfficeCreateRequest;
import com.t1.map_service.dto.office.OfficeDto;
import com.t1.map_service.dto.office.OfficeShortDto;
import com.t1.map_service.dto.office.OfficeUpdateRequest;
import com.t1.map_service.service.OfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offices")
@RequiredArgsConstructor
public class OfficeController implements OfficeApi {

    private final OfficeService service;

    @Override
    @GetMapping("/{officeId}")
    public ResponseEntity<OfficeShortDto> getOffice(
            @PathVariable Long officeId
    ) {
        return ResponseEntity.ok(service.getById(officeId));
    }

    @GetMapping
    public ResponseEntity<List<OfficeDto>> getAllOffices() {
        return ResponseEntity.ok(service.getAll());
    }

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('WORKSPACE_ADMIN')")
    public ResponseEntity<OfficeDto> createOffice(
            @RequestPart("data") @Valid  OfficeCreateRequest request,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, photo));
    }

    @Override
    @PatchMapping(value = "/{officeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@perm.canManageOffice(authentication, #officeId)")
    public ResponseEntity<OfficeDto> updateOffice(
            @PathVariable Long officeId,
            @RequestPart("data") @Valid  OfficeUpdateRequest dto,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) {
        return ResponseEntity.ok(service.update(officeId, dto, photo));
    }

    @Override
    @DeleteMapping("/{officeId}")
    @PreAuthorize("@perm.canManageOffice(authentication, #officeId)")
    public ResponseEntity<Void> deleteOffice(
            @PathVariable Long officeId
    ) {
        service.delete(officeId);
        return ResponseEntity.noContent().build();
    }

}
