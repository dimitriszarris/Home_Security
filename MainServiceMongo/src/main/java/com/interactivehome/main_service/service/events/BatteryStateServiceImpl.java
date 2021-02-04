package com.interactivehome.main_service.service.events;

import com.interactivehome.main_service.model.events.dto.BatteryStateDto;
import com.interactivehome.main_service.model.events.entity.BatteryState;
import com.interactivehome.main_service.repository.events.BatteryStateRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class BatteryStateServiceImpl implements BatteryStateService {
  private final BatteryStateRepository batteryStateRepository;
  private final MongoTemplate mongoTemplate;

  public BatteryStateServiceImpl(BatteryStateRepository batteryStateRepository,
                                 MongoTemplate mongoTemplate) {
    this.batteryStateRepository = batteryStateRepository;
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void saveVoltageBySensorId(Integer id, Integer alarmId, BatteryStateDto dto) {
    BatteryState batteryState = new BatteryState();
    batteryState.mapFromDto(id, dto);
    batteryState.setAlarmId(alarmId);
    batteryStateRepository.save(batteryState);
  }

  @Override
  public List<BatteryState> getVoltageByAlarmIdAndSensorIdFromDateToDate(Integer alarmId, Integer sensorId, Date fromDate, Date toDate) {
    if(fromDate != null && !fromDate.toString().isEmpty() && (toDate == null || toDate.toString().isEmpty())) {
      toDate = java.util.Date.from(LocalDate.now().atStartOfDay().plusDays(1)
          .atZone(ZoneId.systemDefault())
          .toInstant());
    }

    String messageOut;
    messageOut = "Get battery voltage by alarm id: " + alarmId + " and sensor id: " + sensorId;
    if(fromDate != null && toDate != null)
      messageOut += " from date: " + fromDate.toString() + ", to date : " + toDate.toString();
    System.out.println(messageOut);

    Query query = new Query();
    query.addCriteria(Criteria.where("alarm_id").is(alarmId));
    query.addCriteria(Criteria.where("sensor_id").is(sensorId));
    if((fromDate != null && toDate != null) && (!fromDate.toString().isEmpty() && !toDate.toString().isEmpty())) {
      query.addCriteria(Criteria.where("updated_utc").gte(fromDate).lte(toDate));
      query.with(new org.springframework.data.domain.Sort(Direction.DESC, "updated_utc"));
      return mongoTemplate.find(query, BatteryState.class);
    }
    // If the dates are not present then get the latest voltage measurement
    query.with(new org.springframework.data.domain.Sort(Direction.DESC, "updated_utc"));
    List<BatteryState> batteryStateList = new ArrayList<>();
    if(mongoTemplate.find(query, BatteryState.class).size() > 0)
      batteryStateList.add(mongoTemplate.find(query, BatteryState.class).get(0));
    return batteryStateList;
  }
}
