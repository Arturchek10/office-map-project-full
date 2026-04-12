package com.t1.map_service.dto.marker;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.util.Map;

public record UpdateMarkerRequest(
        @NotBlank(message = "Name must exists")
        @Length(max = 255, min = 1, message = "Name must be in 1 to 255 letters length")
        String name,

        @NotBlank(message = "Type must exists")
        String type,

        Boolean uncomfortable,

        Map<String, Object> payload
) {
}
