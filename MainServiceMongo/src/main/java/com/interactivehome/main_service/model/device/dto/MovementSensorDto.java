package com.interactivehome.main_service.model.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovementSensorDto {
    @JsonProperty("movement_sensor_id")
    public Integer movementSensorId;
    @JsonProperty("alarm_id")
    public Integer alarmId;
    public String description;
    @JsonProperty("device_identifier")
    public String deviceIdentifier; // i.e. "Living room 1" or "Kids bedroom 1"
    @JsonProperty("battery_powered")
    public Boolean batteryPowered;
    public Boolean enabled;
}
