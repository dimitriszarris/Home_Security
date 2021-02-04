package com.interactivehome.main_service.repository.events;

import com.interactivehome.main_service.model.events.entity.EnvironmentSensorLastMeasurement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvironmentSensorLastMeasuresRepository extends MongoRepository<EnvironmentSensorLastMeasurement, String> {
}
