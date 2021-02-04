package com.interactivehome.main_service.service.device;


import com.interactivehome.main_service.model.device.dto.EnvironmentSensorDto;
import com.interactivehome.main_service.model.device.entity.EnvironmentSensor;

import java.util.List;

public interface EnvironmentSensorService {
    Integer registerEnvironmentSensor(EnvironmentSensorDto environmentSensorDto, Integer alarmId);
    EnvironmentSensor modifyEnvironmentSensorByAlarmIdAndId(Integer alarmId, Integer id, EnvironmentSensorDto dto);
    EnvironmentSensor getEnvironmentSensorByAlarmIdAndId(Integer alarmId, Integer id);
    List<EnvironmentSensor> getAllEnvironmentSensorsByAlarmId(Integer alarmId);
    void deleteEnvironmentSensorByAlarmIdAndId(Integer alarmId, Integer sensorId);
}
