package com.interactivehome.main_service.model.device.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.interactivehome.main_service.model.device.dto.DoorSensorDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Document(collection = "door_sensor")
public class DoorSensor {
    @Id
    @Field("_id")
    private Integer _id;

    @Field("alarm_id")
    private Integer alarmId;

    @Field("enabled")
    private Boolean enabled;

    @Field("description")
    private String description;

    @Field("device_identifier")
    private String deviceIdentifier;

    @Field("battery_powered")
    private Boolean batteryPowered;

    @Field("trigger_verification_process")
    private Boolean triggerVerificationProcess;

    @Field("arm_in")
    private Boolean armIn;

    @Field("arm_away")
    private Boolean armAway;

    @Field("updated_utc")
    private Date updatedUtc;

    @Field("created_utc")
    private Date createdUtc;

    public void createDoorSensorFromDto(Integer id, DoorSensorDto dto, Integer alarmId) {
        _id = id;
        this.alarmId = alarmId;
        enabled = dto.enabled;
        description = dto.description;
        deviceIdentifier = dto.deviceIdentifier;
        batteryPowered = dto.batteryPowered;
        triggerVerificationProcess = dto.triggerVerificationProcess;
        armIn = dto.armIn;
        armAway = dto.armAway;
        createdUtc = new Date(System.currentTimeMillis());
    }

    public void updateDoorSensorFromDto(DoorSensorDto dto) {
        enabled = dto.enabled;
        description = dto.description;
        deviceIdentifier = dto.deviceIdentifier;
        batteryPowered = dto.batteryPowered;
        triggerVerificationProcess = dto.triggerVerificationProcess;
        armIn = dto.armIn;
        armAway = dto.armAway;
        updatedUtc = new Date(System.currentTimeMillis());
    }
}
