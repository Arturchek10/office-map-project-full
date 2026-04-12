package com.t1.map_service.dto.description;

public record WorkspaceDescriptionDto(
        String text,
        boolean haveComputer
) implements DescriptionDto{
}
