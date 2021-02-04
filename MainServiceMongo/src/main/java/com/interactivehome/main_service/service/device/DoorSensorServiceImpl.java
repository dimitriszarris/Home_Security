package com.interactivehome.main_service.service.device;

import com.interactivehome.main_service.model.device.dto.DoorSensorDto;
import com.interactivehome.main_service.model.device.entity.DoorSensor;
import com.interactivehome.main_service.repository.device.DoorSensorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DoorSensorServiceImpl implements DoorSensorService {

    private final DoorSensorRepository doorSensorRepository;
    private final MongoTemplate mongoTemplate;

    public DoorSensorServiceImpl(DoorSensorRepository doorSensorRepository,
                                      MongoTemplate mongoTemplate) {
        this.doorSensorRepository = doorSensorRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void registerDoorSensor(DoorSensorDto dto, Integer alarmId) {
        DoorSensor doorSensor = new DoorSensor();
        Integer id = getNextId(alarmId);
        doorSensor.createDoorSensorFromDto(id, dto, alarmId);
        mongoTemplate.save(doorSensor);
    }

    private Integer getNextId(Integer alarmId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        query.with(new org.springframework.data.domain.Sort(Sort.Direction.DESC, "_id"));
        if(mongoTemplate.findOne(query, DoorSensor.class) != null)
            return mongoTemplate.findOne(query, DoorSensor.class).get_id() + 1;

        return 1;
    }

    @Override
    public DoorSensor modifyDoorSensorByAlarmIdAndId(Integer alarmId, Integer id, DoorSensorDto dto) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        DoorSensor doorSensor = mongoTemplate.findOne(query, DoorSensor.class);
        if(doorSensor != null) {
            doorSensor.updateDoorSensorFromDto(dto);
            mongoTemplate.save(doorSensor);
        }
        return doorSensor;
    }

    @Override
    public DoorSensor getDoorSensorByAlarmIdAndId(Integer alarmId, Integer id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        return mongoTemplate.findOne(query, DoorSensor.class);
    }

    @Override
    public List<DoorSensor> getAllDoorSensorsByAlarmId(Integer alarmId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        return mongoTemplate.find(query, DoorSensor.class);
    }

    @Override
    public void deleteDoorSensorByAlarmIdAndId(Integer alarmId, Integer id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        mongoTemplate.remove(Objects.requireNonNull(mongoTemplate.findOne(query, DoorSensor.class))).wasAcknowledged();
    }
}
