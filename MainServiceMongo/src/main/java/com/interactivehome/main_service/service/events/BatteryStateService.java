package com.interactivehome.main_service.service.events;

import com.interactivehome.main_service.model.events.dto.BatteryStateDto;
import com.interactivehome.main_service.model.events.entity.BatteryState;
import java.util.Date;
import java.util.List;


public interface BatteryStateService {
  void saveVoltageBySensorId(Integer id, Integer alarmId, BatteryStateDto dto);
  List<BatteryState> getVoltageByAlarmIdAndSensorIdFromDateToDate(Integer alarmId, Integer sensorId, Date fromDate, Date toDate);
}
