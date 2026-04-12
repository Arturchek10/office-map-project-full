package com.t1.map_service.service.inner;

import com.t1.map_service.model.entity.Layer;

public interface LayerInnerService {

    Layer getEntityById(Long layerId);

    void createBaseLayer(Long floorId);
}
