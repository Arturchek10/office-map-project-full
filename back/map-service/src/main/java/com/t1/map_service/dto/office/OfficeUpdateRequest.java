package com.t1.map_service.dto.office;

import org.hibernate.validator.constraints.Length;

public record OfficeUpdateRequest(
        @Length(max = 255, min = 1, message = "Name must be in 1 to 255 letters length")
        String name,
        @Length(max = 255, min = 1, message = "Address must be in 1 to 255 letters length")
        String address,
        Boolean removePhoto
) {
}
