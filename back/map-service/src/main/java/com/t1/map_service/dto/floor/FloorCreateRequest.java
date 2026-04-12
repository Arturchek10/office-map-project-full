package com.t1.map_service.dto.floor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record FloorCreateRequest(
        @NotBlank(message = "Name must exists")
        @Length(max = 255, min = 1, message = "Name must be in 1 to 255 letters length")
        String name,
        @NotNull(message = "Order number must exists")
        Integer orderNumber
) {
}
