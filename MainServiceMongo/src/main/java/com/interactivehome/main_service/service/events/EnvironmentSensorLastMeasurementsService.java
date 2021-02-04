package com.interactivehome.main_service.service.events;

import com.interactivehome.main_service.model.events.entity.EnvironmentSensorLastMeasurement;
import com.interactivehome.main_service.model.events.entity.EnvironmentSensorState;

import java.util.List;

public interface EnvironmentSensorLastMeasurementsService {
    void saveLastMeasurements(Integer alarmId, Integer sensorId, EnvironmentSensorState environmentSensorState);

    List<EnvironmentSensorLastMeasurement> getLastMeasurementsFromAllEnvironmentSensorsByAlarmId(Integer alarmId);
}
