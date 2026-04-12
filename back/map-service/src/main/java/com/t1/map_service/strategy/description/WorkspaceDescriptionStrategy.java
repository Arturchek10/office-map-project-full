package com.t1.map_service.strategy.description;

import com.t1.map_service.enums.MarkerType;
import com.t1.map_service.model.entity.description.Description;
import com.t1.map_service.model.entity.description.WorkspaceDescription;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("workspace")
public class WorkspaceDescriptionStrategy implements DescriptionStrategy {
    @Override
    public MarkerType supports() {
        return MarkerType.WORKSPACE;
    }

    @Override
    public Description build(Map<String, Object> payload) {
        WorkspaceDescription desc = new WorkspaceDescription();
        patch(desc, payload);
        return desc;
    }

    @Override
    public void patch(Description description, Map<String, Object> payload) {
        WorkspaceDescription workspaceDescription = (WorkspaceDescription) description;

        if (payload.containsKey("text")) {
            workspaceDescription.setText((String) payload.get("text"));
        }

        if (payload.containsKey("haveComputer")) {
            workspaceDescription.setHaveComputer((boolean) payload.get("haveComputer"));
        }
    }
}
