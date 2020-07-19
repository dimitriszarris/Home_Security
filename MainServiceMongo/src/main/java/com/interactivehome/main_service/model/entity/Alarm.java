package com.interactivehome.main_service.model.entity;

import com.interactivehome.main_service.model.dto.AlarmDto;
import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@Document(collection = "alarm")
public class Alarm {
  @Id
  @Field("_id")
  private String id;

  @Field("alarm_id")
  private Integer alarmId;

  @Field("alarm_on")
  private Boolean alarmOn;

  @Field("alarm_state")
  private Integer alarmState;

  @Field("updated_utc")
  private Date updatedUtc;

  public void mapFromDto(AlarmDto dto) {
    alarmId = dto.alarmId;
    alarmOn = dto.alarmOn;
    alarmState = dto.alarmState;
    updatedUtc = new Date(System.currentTimeMillis());
  }
}
