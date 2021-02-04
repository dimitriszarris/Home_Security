package com.interactivehome.main_service.model.events.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.interactivehome.main_service.model.events.dto.EnvironmentSensorStateDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Document(collection = "environment_sensor_last_measurement")
public class EnvironmentSensorLastMeasurement {
    @Id
    @Field("_id")
    private Integer _id;

    @Field("alarm_id")
    private Integer alarmId;

    private String description;
    private Float temperature;
    private Float humidity;
    private Float pressure;
    private Float altitude;
    @Field("gas_value")
    private Integer gasValue;
    @Field("updated_utc")
    private Date updatedUtc;

    public void mapFromDto(Integer id, EnvironmentSensorStateDto dto) {
        _id = id;
        temperature = dto.temperature;
        humidity = dto.humidity;
        pressure = dto.pressure;
        altitude = dto.altitude;
        gasValue = dto.gasValue;
        updatedUtc = new Date(System.currentTimeMillis());
    }
}
