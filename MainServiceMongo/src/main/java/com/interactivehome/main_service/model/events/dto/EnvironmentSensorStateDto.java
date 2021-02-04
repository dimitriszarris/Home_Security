package com.interactivehome.main_service.model.events.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnvironmentSensorStateDto {
    @JsonProperty("_id")
    public Integer _id;
    @JsonProperty("alarm_id")
    public Integer alarmId;
    public Float temperature;
    public Float humidity;
    public Float pressure;
    public Float altitude;
    @JsonProperty("gas_value")
    public Integer gasValue;
    @JsonProperty("updated_utc")
    public Date updatedUtc;
}
