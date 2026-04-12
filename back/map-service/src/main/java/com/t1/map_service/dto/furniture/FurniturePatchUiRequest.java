package com.t1.map_service.dto.furniture;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record FurniturePatchUiRequest(
        @Max(value = 359, message = "Angle cannot be more than 359")
        Integer angle,

        @Positive(message = "Size factor must be positive")
        Short sizeFactor
) {
}
