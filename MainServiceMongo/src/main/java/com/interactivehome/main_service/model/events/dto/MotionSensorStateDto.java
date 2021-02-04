package com.interactivehome.main_service.model.events.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MotionSensorStateDto {
    @JsonProperty("_id")
    public Integer _id;
    @JsonProperty("alarm_id")
    public Integer alarmId;
    @JsonProperty("motion_caught")
    public Boolean motionCaught;
    @JsonProperty("updated_utc")
    public Date updatedUtc;
}
