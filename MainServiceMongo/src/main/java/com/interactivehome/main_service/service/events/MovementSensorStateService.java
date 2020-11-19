package com.interactivehome.main_service.service.events;

import com.interactivehome.main_service.model.events.dto.MotionSensorStateDto;
import com.interactivehome.main_service.model.events.entity.MotionSensorState;
import java.util.Date;
import java.util.List;

public interface MovementSensorStateService {
  void saveState(MotionSensorStateDto dto);
  List<MotionSensorState> getSensorActivityByAlarmIdAndSensorId(Integer alarmId, Integer sensorId, Date fromDate, Date toDate);

  void buzzAlarm();
}
