// КОПИПАСТА

package com.t1.map_service.mapper;

import com.t1.map_service.dto.description.*;
import com.t1.map_service.model.entity.description.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DescriptionMapper {

    // Полиморфный маппинг
    default DescriptionDto toDto(Description src) {
        if (src == null) return null;

        if (src instanceof RoomDescription r) {
            return new RoomDescriptionDto(
                    r.getText(),
                    r.getCapacity()
            );
        }
        if (src instanceof WorkspaceDescription w) {
            return new WorkspaceDescriptionDto(
                    w.getText(),
                    w.getHaveComputer()
            );
        }

        if (src instanceof UtilityDescription u) {
            return new UtilityDescriptionDto(
                    u.getText()
            );
        }

        if (src instanceof EmergencyDescription e) {
            return new EmergencyDescriptionDto(
                    e.getText()
            );
        }
        throw new IllegalArgumentException("Unknown Description subtype: " + src.getClass());
    }

    // Маппинг напрямую
    default RoomDescriptionDto toDto(RoomDescription r) {
        if (r == null) return null;
        return new RoomDescriptionDto(
                r.getText(),
                r.getCapacity()
        );
    }

    default WorkspaceDescriptionDto toDto(WorkspaceDescription w) {
        if (w == null) return null;
        return new WorkspaceDescriptionDto(
                w.getText(),
                w.getHaveComputer()
        );
    }

    default UtilityDescriptionDto toDto(UtilityDescription u) {
        if (u == null) return null;
        return new UtilityDescriptionDto(
                u.getText()
        );
    }

    default EmergencyDescriptionDto toDto(EmergencyDescription e) {
        if (e == null) return null;
        return new EmergencyDescriptionDto(
                e.getText()
        );
    }
}