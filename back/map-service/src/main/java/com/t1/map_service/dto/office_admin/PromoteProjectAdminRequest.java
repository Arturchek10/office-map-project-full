package com.t1.map_service.dto.office_admin;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record PromoteProjectAdminRequest(
        @NotBlank(message = "Login must be exists")
        @Length(min = 1, max = 255, message = "Login must be in 1 to 255 letters length")
        String login
) {
}
