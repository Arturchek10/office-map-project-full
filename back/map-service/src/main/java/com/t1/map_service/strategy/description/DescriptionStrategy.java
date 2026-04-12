package com.t1.map_service.strategy.description;

import com.t1.map_service.enums.MarkerType;
import com.t1.map_service.model.entity.description.Description;

import java.util.Map;

public interface DescriptionStrategy {

    MarkerType supports();
    Description build(Map<String, Object> payload);
    void patch(Description description, Map<String, Object> payload);
}
