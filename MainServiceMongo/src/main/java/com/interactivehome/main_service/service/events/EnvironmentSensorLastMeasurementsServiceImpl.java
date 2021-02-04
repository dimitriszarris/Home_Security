package com.interactivehome.main_service.service.events;

import com.interactivehome.main_service.model.device.entity.EnvironmentSensor;
import com.interactivehome.main_service.model.events.entity.EnvironmentSensorLastMeasurement;
import com.interactivehome.main_service.model.events.entity.EnvironmentSensorState;
import com.interactivehome.main_service.repository.events.EnvironmentSensorLastMeasuresRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EnvironmentSensorLastMeasurementsServiceImpl implements EnvironmentSensorLastMeasurementsService {
    private final EnvironmentSensorLastMeasuresRepository environmentSensorLastMeasuresRepository;
    private final MongoTemplate mongoTemplate;

    public EnvironmentSensorLastMeasurementsServiceImpl(EnvironmentSensorLastMeasuresRepository environmentSensorLastMeasuresRepository,
                                                        MongoTemplate mongoTemplate) {
        this.environmentSensorLastMeasuresRepository = environmentSensorLastMeasuresRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void saveLastMeasurements(Integer alarmId, Integer sensorId, EnvironmentSensorState environmentSensorState) {
        Query query = new Query();
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        query.addCriteria(Criteria.where("_id").is(sensorId));
        EnvironmentSensor environmentSensor = mongoTemplate.findOne(query, EnvironmentSensor.class);
        var description = "";
        if(environmentSensor != null)
            description = environmentSensor.getDescription();

        EnvironmentSensorLastMeasurement lastMeasurements = mongoTemplate.findOne(query, EnvironmentSensorLastMeasurement.class);
        if(lastMeasurements == null) {
            lastMeasurements = new EnvironmentSensorLastMeasurement();
            lastMeasurements.set_id(sensorId);
            lastMeasurements.setAlarmId(alarmId);
            lastMeasurements.setDescription(description);
        }
        lastMeasurements.setTemperature(environmentSensorState.getTemperature());
        lastMeasurements.setHumidity(environmentSensorState.getHumidity());
        lastMeasurements.setGasValue(environmentSensorState.getGasValue());
        lastMeasurements.setAltitude(environmentSensorState.getAltitude());
        lastMeasurements.setPressure(environmentSensorState.getPressure());
        lastMeasurements.setUpdatedUtc(new Date(System.currentTimeMillis()));
        environmentSensorLastMeasuresRepository.save(lastMeasurements);
    }

    @Override
    public List<EnvironmentSensorLastMeasurement> getLastMeasurementsFromAllEnvironmentSensorsByAlarmId(Integer alarmId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        return mongoTemplate.find(query, EnvironmentSensorLastMeasurement.class);
    }
}
