package com.t1.map_service.strategy.description;

import com.t1.map_service.enums.MarkerType;
import com.t1.map_service.model.entity.description.Description;
import com.t1.map_service.model.entity.description.UtilityDescription;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("utility")
public class UtilityDescriptionStrategy implements DescriptionStrategy {

    @Override
    public MarkerType supports() {
        return MarkerType.UTILITY;
    }

    @Override
    public Description build(Map<String, Object> payload) {
        UtilityDescription utilityDescription = new UtilityDescription();
        patch(utilityDescription, payload);
        return utilityDescription;
    }

    @Override
    public void patch(Description description, Map<String, Object> payload) {
        UtilityDescription utilityDescription = (UtilityDescription) description;

        if (payload.containsKey("text")) {
            utilityDescription.setText((String) payload.get("text"));
        }
    }
}
