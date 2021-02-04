package com.interactivehome.main_service.service.events;

import com.interactivehome.main_service.model.events.dto.EnvironmentSensorStateDto;
import com.interactivehome.main_service.model.events.entity.EnvironmentSensorState;
import java.util.Date;
import java.util.List;

public interface EnvironmentSensorStateService {
  void saveValuesBySensorId(Integer id, Integer alarmId, EnvironmentSensorStateDto dto);

  List<EnvironmentSensorStateDto> getValuesByAlarmIdAndSensorIdFromDateToDate(
      Integer alarmId, Integer sensorId, Date fromDate, Date toDate);
}
