package com.interactivehome.main_service.model.device.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.interactivehome.main_service.model.device.dto.EnvironmentSensorDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Document(collection = "environment_sensor")
public class EnvironmentSensor {
    @Id
    @Field("_id")
    private Integer _id;

    @Field("alarm_id")
    private Integer alarmId;

    private String description;
    @Field("device_identifier")
    private String deviceIdentifier; // i.e. "Living room 1" or "Kids bedroom 1"

    @Field("battery_powered")
    private Boolean batteryPowered;
    @Field("enabled")
    private Boolean enabled;
    @Field("arm_in")
    private Boolean armIn;
    @Field("arm_away")
    private Boolean armAway;

    @Field("updated_utc")
    private Date updatedUtc;
    @Field("created_utc")
    private Date createdUtc;

    public void createEnvironmentSensorFromDto(Integer id, EnvironmentSensorDto dto, Integer alarmId) {
        _id = id;
        this.alarmId = alarmId;
        enabled = dto.enabled;
        description = dto.description;
        deviceIdentifier = dto.deviceIdentifier;
        batteryPowered = dto.batteryPowered;
        armIn = dto.armIn;
        armAway = dto.armAway;
        createdUtc = new Date(System.currentTimeMillis());
    }

    public void updateEnvironmentSensorFromDto(EnvironmentSensorDto dto) {
        enabled = dto.enabled;
        description = dto.description;
        deviceIdentifier = dto.deviceIdentifier;
        batteryPowered = dto.batteryPowered;
        armIn = dto.armIn;
        armAway = dto.armAway;
        updatedUtc = new Date(System.currentTimeMillis());
    }
}
