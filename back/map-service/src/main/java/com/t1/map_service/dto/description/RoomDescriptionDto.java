package com.t1.map_service.dto.description;

public record RoomDescriptionDto(
        String text,
        Integer capacity
) implements DescriptionDto{
}
