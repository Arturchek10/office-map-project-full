package com.t1.map_service.config;

import com.t1.map_service.model.Point;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "marker")
public class MarkerProperties {

    private final DefaultPosition defaultPosition = new DefaultPosition();

    public Point getDefaultPosition() {
        return new Point(defaultPosition.getX(), defaultPosition.getY());
    }

    @Getter
    @Setter
    public static class DefaultPosition {
        private double x;
        private double y;
    }
}
