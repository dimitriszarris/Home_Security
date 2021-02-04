package com.interactivehome.main_service.service.device;

import com.interactivehome.main_service.model.device.dto.MotionSensorDto;
import com.interactivehome.main_service.model.device.entity.MotionSensor;

import java.util.List;

public interface MotionSensorService {
    void registerMotionSensor(MotionSensorDto motionSensorDto, Integer alarmId);
    MotionSensor modifyMotionSensorByAlarmIdAndId(Integer alarmId, Integer id, MotionSensorDto motionSensorDto);
    MotionSensor getMotionSensorByAlarmIdAndId(Integer alarmId, Integer id);
    List<MotionSensor> getAllMotionSensorsByAlarmId(Integer alarmId);
    void deleteMotionSensor(Integer alarmId, Integer id);
}
