package com.t1.map_service.service;

import com.t1.map_service.dto.floor.FloorViewDto;

public interface FloorViewService {

    FloorViewDto getFloorView(Long floorId);
}
