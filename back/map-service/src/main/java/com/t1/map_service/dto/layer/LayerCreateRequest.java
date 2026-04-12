package com.t1.map_service.dto.layer;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LayerCreateRequest(
        @NotBlank(message = "Name must exists")
        @Length(max = 255, min = 1, message = "Name must be in 1 to 255 letters length")
        String name
) {
}
