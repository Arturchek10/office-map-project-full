package com.t1.map_service.strategy.description;

import com.t1.map_service.enums.MarkerType;
import com.t1.map_service.model.entity.description.Description;
import com.t1.map_service.model.entity.description.RoomDescription;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("room")
public class RoomDescriptionStrategy implements DescriptionStrategy {

    @Override
    public MarkerType supports() {
        return MarkerType.ROOM;
    }

    @Override
    public Description build(Map<String, Object> payload) {
        RoomDescription desc = new RoomDescription();
        patch(desc, payload);
        return desc;
    }

    @Override
    public void patch(Description description, Map<String, Object> payload) {
        RoomDescription roomDescription = (RoomDescription) description;

        if (payload.containsKey("capacity")){
            roomDescription.setCapacity((Integer) payload.get("capacity"));
        }

        if (payload.containsKey("text")){
            roomDescription.setText((String) payload.get("text"));
        }
    }
}
