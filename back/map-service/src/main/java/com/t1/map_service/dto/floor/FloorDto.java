package com.t1.map_service.dto.floor;

public record FloorDto(
        Long id,
        String name,
        String photoUrl,
        Integer orderNumber
) {
}
