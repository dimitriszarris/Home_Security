package com.interactivehome.main_service.service.device;

import com.interactivehome.main_service.model.device.dto.EnvironmentSensorDto;
import com.interactivehome.main_service.model.device.entity.EnvironmentSensor;
import com.interactivehome.main_service.model.device.entity.MotionSensor;
import com.interactivehome.main_service.model.events.entity.TemperatureHumidityGasSensorState;
import com.interactivehome.main_service.repository.device.TemperatureHumidityGasSensorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class EnvironmentSensorServiceImpl implements EnvironmentSensorService {

    private final TemperatureHumidityGasSensorRepository temperatureHumidityGasSensorRepository;
    private final MongoTemplate mongoTemplate;

    public EnvironmentSensorServiceImpl(TemperatureHumidityGasSensorRepository temperatureHumidityGasSensorRepository,
                                        MongoTemplate mongoTemplate) {
        this.temperatureHumidityGasSensorRepository = temperatureHumidityGasSensorRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void registerEnvironmentSensor(EnvironmentSensorDto dto) {
        EnvironmentSensor environmentSensor = new EnvironmentSensor();
        Integer id = getNextId();
        environmentSensor.createEnvironmentSensorFromDto(id, dto);
        mongoTemplate.save(environmentSensor);
    }

    private Integer getNextId() {
        Query query = new Query();
        query.with(new org.springframework.data.domain.Sort(Sort.Direction.DESC, "_id"));
        if(mongoTemplate.findOne(query, EnvironmentSensor.class) != null)
            return mongoTemplate.findOne(query, EnvironmentSensor.class).get_id() + 1;

        return 1;
    }

    @Override
    public EnvironmentSensor modifyEnvironmentSensorByAlarmIdAndId(Integer alarmId, Integer id, EnvironmentSensorDto dto) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        EnvironmentSensor environmentSensor = mongoTemplate.findOne(query, EnvironmentSensor.class);
        if(environmentSensor != null) {
            environmentSensor.updateEnvironmentSensorFromDto(dto);
            mongoTemplate.save(environmentSensor);
        }
        return environmentSensor;
    }

    @Override
    public EnvironmentSensor getEnvironmentSensorByAlarmIdAndId(Integer alarmId, Integer id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        return mongoTemplate.findOne(query, EnvironmentSensor.class);
    }

    @Override
    public List<EnvironmentSensor> getAllEnvironmentSensorsByAlarmId(Integer alarmId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        return mongoTemplate.find(query, EnvironmentSensor.class);
    }

    @Override
    public void deleteEnvironmentSensorByAlarmIdAndId(Integer alarmId, Integer id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        mongoTemplate.remove(Objects.requireNonNull(mongoTemplate.findOne(query, EnvironmentSensor.class))).wasAcknowledged();
    }
}
