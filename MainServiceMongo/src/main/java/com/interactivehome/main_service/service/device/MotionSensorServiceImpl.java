package com.interactivehome.main_service.service.device;

import com.interactivehome.main_service.model.device.dto.MotionSensorDto;
import com.interactivehome.main_service.model.device.entity.MotionSensor;
import com.interactivehome.main_service.repository.device.MotionSensorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MotionSensorServiceImpl implements MotionSensorService {

    private final MotionSensorRepository motionSensorRepository;
    private final MongoTemplate mongoTemplate;

    public MotionSensorServiceImpl(MotionSensorRepository motionSensorRepository,
                                   MongoTemplate mongoTemplate) {
        this.motionSensorRepository = motionSensorRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void registerMotionSensor(MotionSensorDto dto, Integer alarmId) {
        MotionSensor motionSensor = new MotionSensor();
        Integer id = getNextId(alarmId);
        motionSensor.createMotionSensorFromDto(id, dto, alarmId);
        mongoTemplate.save(motionSensor);
    }

    private Integer getNextId(Integer alarmId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        query.with(new org.springframework.data.domain.Sort(Sort.Direction.DESC, "_id"));
        if (mongoTemplate.findOne(query, MotionSensor.class) != null)
            return mongoTemplate.findOne(query, MotionSensor.class).get_id() + 1;

        return 1;
    }

    @Override
    public MotionSensor modifyMotionSensorByAlarmIdAndId(Integer alarmId, Integer id, MotionSensorDto dto) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        MotionSensor motionSensor = mongoTemplate.findOne(query, MotionSensor.class);
        if (motionSensor != null) {
            motionSensor.updateMotionSensorFromDto(dto);
            mongoTemplate.save(motionSensor);
        } else {
            // throw Exception()
        }
        return motionSensor;
    }

    @Override
    public MotionSensor getMotionSensorByAlarmIdAndId(Integer alarmId, Integer id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        return mongoTemplate.findOne(query, MotionSensor.class);
    }

    @Override
    public List<MotionSensor> getAllMotionSensorsByAlarmId(Integer alarmId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        return mongoTemplate.find(query, MotionSensor.class);
    }

    @Override
    public void deleteMotionSensor(Integer alarmId, Integer id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        mongoTemplate.remove(Objects.requireNonNull(mongoTemplate.findOne(query, MotionSensor.class))).wasAcknowledged();
    }
}
