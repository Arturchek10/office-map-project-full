package com.t1.map_service.service.inner;

import com.t1.map_service.model.entity.Marker;

import java.util.List;

public interface MarkerInnerService {

    Marker getEntityById(Long markerId);

    List<Marker> getListByLayerIdWithFilter(Long layerId, boolean hideUncomfortable);
}
