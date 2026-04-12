package com.t1.map_service.observer.listener;

import com.t1.map_service.observer.event.FloorCreatedEvent;
import com.t1.map_service.service.inner.LayerInnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BaseLayerListener {

    private final LayerInnerService service;

    @EventListener
    public void onFloorCreated(FloorCreatedEvent event) {
        service.createBaseLayer(event.layerId());
    }
}
