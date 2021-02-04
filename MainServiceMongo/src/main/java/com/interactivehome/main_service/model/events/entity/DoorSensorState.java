package com.interactivehome.main_service.model.events.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.interactivehome.main_service.model.events.dto.DoorSensorStateDto;

import java.util.Date;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Document(collection = "door_sensor_state")
public class DoorSensorState {
    @Id
    @Field("_id")
    private ObjectId _id;

    @Field("sensor_id")
    private Integer sensorId;

    @Field("alarm_id")
    private Integer alarmId;

    @Field("door_state")
    private Boolean doorState;

    @Field("updated_utc")
    private Date updatedUtc;

    public void mapFromDto(DoorSensorStateDto dto) {
        doorState = dto.doorState;
        updatedUtc = new Date(System.currentTimeMillis());
    }
}
