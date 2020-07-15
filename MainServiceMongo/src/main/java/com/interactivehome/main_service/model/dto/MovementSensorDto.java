package com.interactivehome.main_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MovementSensorDto {
    @JsonProperty("movement_sensor_id")
    private Integer movementSensorId;
    @JsonProperty("movement_sensor_state")
    private Boolean movementSensorState;
    @JsonProperty("movement_sensor_activated")
    private Boolean movementSensorActivated;
}
