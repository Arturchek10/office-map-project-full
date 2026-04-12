package com.t1.map_service.exception.dto;

import lombok.Builder;

@Builder
public record SubError(
        String object,
        String field,
        Object rejectedValue,
        String message
) {
}
