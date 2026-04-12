package com.t1.map_service.service.facade;

import com.t1.map_service.dto.floor.*;
import com.t1.map_service.service.FloorService;
import com.t1.map_service.service.FloorViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FloorFacade {

    private final FloorService floorService;
    private final FloorViewService floorViewService;

    public FloorViewDto createFloorAndView(FloorCreateRequest dto, Long officeId) {
        log.info("Facade: create floor and view (officeId={}, orderNumber={})",
                officeId, dto.orderNumber());

        Long floorId = floorService.create(dto, officeId);
        FloorViewDto floorViewDto = floorViewService.getFloorView(floorId);

        log.info("Facade: floor created and view returned (floorId={}, officeId={})",
                floorId, officeId);
        return floorViewDto;
    }

    public FloorViewDto updateFloorAndView(Long floorId, FloorUpdateRequest dto) {
        log.info("Facade: update floor and view (floorId={})", floorId);

        floorService.update(floorId, dto);
        FloorViewDto floorViewDto = floorViewService.getFloorView(floorId);

        log.info("Facade: floor updated and view returned (floorId={})", floorId);
        return floorViewDto;
    }

    public FloorViewDto updateFloorPlanAndView(Long floorId, FloorPlanPatchRequest request, MultipartFile photo) {
        log.info("Facade: upload image floor and view (floorId={})", floorId);

        floorService.updateImage(floorId, request, photo);
        FloorViewDto floorViewDto = floorViewService.getFloorView(floorId);

        log.info("Facade: image uploaded and view returned (floorId={})", floorId);
        return floorViewDto;
    }

}
