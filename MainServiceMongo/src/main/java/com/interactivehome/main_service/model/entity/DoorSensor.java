package com.interactivehome.main_service.model.entity;

import com.interactivehome.main_service.model.dto.DoorSensorDto;
import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "door_state")
public class DoorSensor {
    @Id
    @Field("_id")
    private String id;

    @Field("door_id")
    private Integer doorId;

    @Field("door_state")
    private Boolean doorState;

    @Field("updated_utc")
    private Date updateUtc;

    public void mapFromDto(DoorSensorDto dto) {
        doorId = dto.doorId;
        doorState = dto.doorState;
        updateUtc = new Date(System.currentTimeMillis());
    }
}
