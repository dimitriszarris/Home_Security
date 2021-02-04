package com.interactivehome.main_service.service.device;

import com.interactivehome.main_service.model.device.dto.DoorSensorDto;
import com.interactivehome.main_service.model.device.entity.DoorSensor;

import java.util.List;

public interface DoorSensorService {
    void registerDoorSensor(DoorSensorDto doorSensorDto, Integer alarmId);
    DoorSensor modifyDoorSensorByAlarmIdAndId(Integer alarmId, Integer id, DoorSensorDto doorSensorDto);
    DoorSensor getDoorSensorByAlarmIdAndId(Integer alarmId, Integer id);
    List<DoorSensor> getAllDoorSensorsByAlarmId(Integer alarmId);
    void deleteDoorSensorByAlarmIdAndId(Integer alarmId, Integer id);
}
