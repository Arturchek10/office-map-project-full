package com.t1.map_service.security.service;

import com.t1.map_service.repository.*;
import com.t1.map_service.security.repository.OfficeAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("perm")
@RequiredArgsConstructor
public class PermissionService {

    private final FloorRepository floorRepository;
    private final LayerRepository layerRepository;
    private final MarkerRepository markerRepository;
    private final FurnitureRepository furnitureRepository;
    private final OfficeAdminRepository officeAdminRepository;

    /**
     * Проверка, может ли текущий пользователь управлять офисом
     */
    public boolean canManageOffice(Authentication authentication, Long officeId) {
        if (authentication == null || officeId == null) return false;

        String login = (String) authentication.getPrincipal();

        // WORKSPACE_ADMIN = полный доступ
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("WORKSPACE_ADMIN"))) {
            return true;
        }

        // PROJECT_ADMIN = только в пределах своих офисов
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("PROJECT_ADMIN"))) {
            return officeAdminRepository.existsByLoginAndOfficeId(login, officeId);
        }

        // USER = нет прав на управление
        return false;
    }

    public boolean canManageFloor(Authentication authentication, Long floorId) {
        return floorRepository.findOfficeIdByFloorId(floorId)
                .map(officeId -> canManageOffice(authentication, officeId))
                .orElse(false);
    }

    public boolean canManageLayer(Authentication authentication, Long layerId) {
        return layerRepository.findOfficeIdByLayerId(layerId)
                .map(officeId -> canManageOffice(authentication, officeId))
                .orElse(false);
    }

    public boolean canManageMarker(Authentication authentication, Long markerId) {
        return markerRepository.findOfficeIdByMarkerId(markerId)
                .map(officeId -> canManageOffice(authentication, officeId))
                .orElse(false);
    }

    public boolean canManageFurniture(Authentication authentication, Long furnitureId) {
        return furnitureRepository.findOfficeIdByFurnitureId(furnitureId)
                .map(officeId -> canManageOffice(authentication, officeId))
                .orElse(false);
    }

    public boolean canRevokeProjectAdmin(Authentication authentication, Long officeId, String login) {
        if (authentication == null || officeId == null || login == null) return false;

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("WORKSPACE_ADMIN"))) {
            return true;
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("PROJECT_ADMIN"))) {
            String current = authentication.getName();
            return officeAdminRepository.findByLoginAndOfficeId(login, officeId)
                    .map(record -> record.getCreatedBy().equals(current))
                    .orElse(false);
        }

        return false;
    }
}
