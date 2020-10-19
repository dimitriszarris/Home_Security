package com.interactivehome.main_service.repository;

import com.interactivehome.main_service.model.entity.DoorSensorState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoorSensorStateRepository extends MongoRepository<DoorSensorState, String> {
}
