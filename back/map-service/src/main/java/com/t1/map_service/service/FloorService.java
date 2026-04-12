package com.t1.map_service.service;

import com.t1.map_service.dto.floor.*;
import org.springframework.web.multipart.MultipartFile;

public interface FloorService {

    Long create(FloorCreateRequest dto, Long officeId);

    void update(Long floorId, FloorUpdateRequest dto);

    void updateImage(Long floorId, FloorPlanPatchRequest dto, MultipartFile photo);

    void delete(Long floorId);


}
