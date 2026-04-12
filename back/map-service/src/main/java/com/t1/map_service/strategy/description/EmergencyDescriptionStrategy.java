package com.t1.map_service.strategy.description;

import com.t1.map_service.enums.MarkerType;
import com.t1.map_service.model.entity.description.Description;
import com.t1.map_service.model.entity.description.EmergencyDescription;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("emergency")
public class EmergencyDescriptionStrategy implements DescriptionStrategy {
    @Override
    public MarkerType supports() {
        return MarkerType.EMERGENCY;
    }

    @Override
    public Description build(Map<String, Object> payload) {
        EmergencyDescription emergencyDescription = new EmergencyDescription();
        patch(emergencyDescription, payload);
        return emergencyDescription;
    }

    @Override
    public void patch(Description description, Map<String, Object> payload) {
        EmergencyDescription emergencyDescription = (EmergencyDescription) description;

        if (payload.containsKey("text")) {
            emergencyDescription.setText((String) payload.get("text"));
        }
    }
}
