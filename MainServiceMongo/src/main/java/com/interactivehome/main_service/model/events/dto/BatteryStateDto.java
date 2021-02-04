package com.interactivehome.main_service.model.events.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatteryStateDto {
  @JsonProperty("_id")
  public Integer _id;
  @JsonProperty("alarm_id")
  public Integer alarmId;
  @JsonProperty("battery_voltage")
  public Float batteryVoltage;
  @JsonProperty("battery_percentage")
  public Integer batteryPercentage;
  @JsonProperty("updated_utc")
  public Date updatedUtc;
}
