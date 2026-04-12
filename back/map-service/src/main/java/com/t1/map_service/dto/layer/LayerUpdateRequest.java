package com.t1.map_service.dto.layer;

import org.hibernate.validator.constraints.Length;

public record LayerUpdateRequest(
        @Length(max = 255, min = 1, message = "Name must be in 1 to 255 letters length")
        String name
        // TODO доп.поля
) {
}
