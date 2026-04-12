package com.t1.map_service.enums;

import lombok.Getter;

public enum MarkerType {
    WORKSPACE("workspace"),
    ROOM("room"),
    UTILITY("utility"),
    EMERGENCY("emergency");

    @Getter
    private final String value;

    MarkerType(String value) {
        this.value = value;
    }


}
