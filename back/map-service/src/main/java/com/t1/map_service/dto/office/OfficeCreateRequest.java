package com.t1.map_service.dto.office;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

public record OfficeCreateRequest(
        @NotBlank(message = "Name must exists")
        @Length(max = 255, min = 1, message = "Name must be in 1 to 255 letters length")
        String name, // TODO принимать только строку
        @NotBlank(message = "Address must be exists")
        @Length(max = 255, min = 1, message = "Address must be in 1 to 255 letters length")
        String address,

        @NotNull(message = "Latitude must exist")
        @DecimalMin(value = "-90.0", message = "Latitude must be greater or equal to -90")
        @DecimalMax(value = "90.0", message = "Latitude must be less or equal to 90")
        Double latitude,

        @NotNull(message = "Longitude must exist")
        @DecimalMin(value = "-180.0", message = "Longitude must be greater or equal to -180")
        @DecimalMax(value = "180.0", message = "Longitude must be less or equal to 180")
        Double longitude,

        @NotBlank(message = "City must exists")
        @Length(max = 255, min = 1, message = "City name must be in 1 to 255 letters length")
        String city
) {
}
