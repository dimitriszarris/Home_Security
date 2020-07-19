package com.interactivehome.main_service.service;

import com.interactivehome.main_service.model.dto.TemperatureHumidiryGasDto;
import com.interactivehome.main_service.model.entity.TemperatureHumidityGas;
import com.interactivehome.main_service.repository.TemperatureHumidityGasRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class TemperatureHumidityGasServiceImpl implements TemperatureHumidityGasService {
  private final TemperatureHumidityGasRepository temperatureHumidityGasRepository;

  private final MongoTemplate mongoTemplate;

  public TemperatureHumidityGasServiceImpl(TemperatureHumidityGasRepository temperatureHumidityGasRepository,
                                            MongoTemplate mongoTemplate) {
    this.temperatureHumidityGasRepository = temperatureHumidityGasRepository;
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void saveValues(TemperatureHumidiryGasDto dto) {
    TemperatureHumidityGas temperatureHumidityGas = new TemperatureHumidityGas();
    temperatureHumidityGas.mapFromDto(dto);
    temperatureHumidityGasRepository.save(temperatureHumidityGas);
  }

  @Override
  public List<TemperatureHumidityGas> getValuesBySensorIdFromDateToDate(
                                          Integer sensorId, Date fromDate, Date toDate) {
    if(fromDate != null && !fromDate.toString().isEmpty() && (toDate == null || toDate.toString().isEmpty()))
      toDate = java.util.Date.from(LocalDate.now().atStartOfDay()
          .atZone(ZoneId.systemDefault())
          .toInstant());

    String messageOut;
    messageOut = "Get temperature humidity gas values by sensro id: " + sensorId;
    if(fromDate != null && toDate != null)
      messageOut += " from date: " + fromDate.toString() + ", to date : " + toDate.toString();
    System.out.println(messageOut);

    Query query = new Query();
    query.addCriteria(Criteria.where("sensorId").is(sensorId));
    if((fromDate != null && toDate != null) && (!fromDate.toString().isEmpty() && !toDate.toString().isEmpty())) {
      query.addCriteria(Criteria.where("updatedUtc").gte(fromDate).lt(toDate));
      query.with(new Sort(Direction.DESC, "updatedUtc"));
      return mongoTemplate.find(query, TemperatureHumidityGas.class);
    }
    if((fromDate != null && toDate != null) && (!fromDate.toString().isEmpty() && !toDate.toString().isEmpty())) {
      query.addCriteria(Criteria.where("updatedUtc").gte(fromDate).lt(toDate));
      query.with(new Sort(Direction.DESC, "updatedUtc"));
      return mongoTemplate.find(query, TemperatureHumidityGas.class);
    }
    // If the dates are not present then get the latest temperature humidity gas measurements
    query.with(new Sort(Direction.DESC, "updatedUtc"));
    List<TemperatureHumidityGas> temperatureHumidityGasList = null;
    temperatureHumidityGasList.add(mongoTemplate.find(query, TemperatureHumidityGas.class).get(0));
    return temperatureHumidityGasList;
  }
}
