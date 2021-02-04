package com.interactivehome.main_service.service.events;

import com.interactivehome.main_service.config.AppProperties;
import com.interactivehome.main_service.model.events.dto.AlarmSystemStateDto;
import com.interactivehome.main_service.model.events.entity.AlarmSystemState;
import com.interactivehome.main_service.repository.events.AlarmSystemStateRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.micrometer.core.instrument.util.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class AlarmSystemStateServiceImpl implements AlarmSystemStateService {
  private final AlarmSystemStateRepository alarmSystemStateRepository;
  private final MongoTemplate mongoTemplate;

  @Autowired
  private AppProperties appProperties;

  @Value("$(securityControllerIpPort)")
  private String securityControllerIpPort;
  @Value("$(alarmStateEndpoint)")
  private String alarmStateEndpoint;
  @Value("$(stopAlarmEndpoint)")
  private String stopAlarmEndpoint;

  public AlarmSystemStateServiceImpl(AlarmSystemStateRepository alarmSystemStateRepository,
                                     MongoTemplate mongoTemplate) {
    this.alarmSystemStateRepository = alarmSystemStateRepository;
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void saveAlarmStateById(Integer id, AlarmSystemStateDto dto) {
    var securityControllerNotUpdated = false;
    AlarmSystemState alarmSystemState = new AlarmSystemState();
    alarmSystemState.mapFromDto(id, dto);
    alarmSystemStateRepository.save(alarmSystemState);
/*    while(securityControllerNotUpdated) {
      try {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        try {
          jsonObject.put("system_alarm_state", dto.getAlarmState());
        }
        catch (JSONException e) {
          System.out.println("Error while building json value 'alarm_state'. " + e.toString());
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonObject.toString(), requestHeaders);
        System.out.println("Security controller alarm state endpoint: " +
                appProperties.getSecurityControllerIpPort() + appProperties.getAlarmStateEndpoint() +
                ". \n Payload: \n" + jsonObject.toString());
        ResponseEntity<String> responseEntity = restTemplate.
                postForEntity(appProperties.getSecurityControllerIpPort() +
                                appProperties.getAlarmStateEndpoint(),
                        requestEntity,
                        String.class);

        if(responseEntity.getStatusCode() == HttpStatus.OK)
        {
          // Update the alarm state in the database only when the security controller has been updated successfully
          System.out.println("The alarm state in the security controller has been updated successfully");
          alarmSystemStateRepository.save(alarmSystemState);
          securityControllerNotUpdated = true;
        }
        else
        {
          System.out.println("Error response from security controller while trying to update the alarm state. " +
                  responseEntity.getStatusCode() + ": " + responseEntity.getBody());
        }
      }
      catch (RuntimeException exception) {
        System.out.println("Runtime exception: " + exception.toString());
        JSONObject payload = new JSONObject();
        try {
          payload.put("message", "The security controller alarm state could not get updated!");
          payload.put("error", StringEscapeUtils.escapeJson(exception.toString()));
        } catch (JSONException e) {
          System.out.println(e.toString());
        }
        // return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  */
  }

  @Override
  public List<AlarmSystemState> getAlarmStateByIdFromDateToDate(Integer id, Date fromDate, Date toDate) {
    if(fromDate != null && !fromDate.toString().isEmpty() && (toDate == null || toDate.toString().isEmpty())) {
      toDate = java.util.Date.from(LocalDate.now().atStartOfDay().plusDays(1)
          .atZone(ZoneId.systemDefault())
          .toInstant());
    }

    String messageOut;
    messageOut = "Get alarm state by alarm id: " + id;
    if(fromDate != null && toDate != null)
      messageOut += " from date: " + fromDate.toString() + ", to date : " + toDate.toString();
    System.out.println(messageOut);

    Query query = new Query();
    query.addCriteria(Criteria.where("_id").is(id));
    if((fromDate != null && toDate != null) && (!fromDate.toString().isEmpty() && !toDate.toString().isEmpty())) {
      query.addCriteria(Criteria.where("updated_utc").gte(fromDate).lte(toDate));
      query.with(new org.springframework.data.domain.Sort(Direction.DESC, "updated_utc"));
      return mongoTemplate.find(query, AlarmSystemState.class);
    }
    // If the dates are not present then get the latest voltage measurement
    query.with(new org.springframework.data.domain.Sort(Direction.DESC, "updated_utc"));
    List<AlarmSystemState> alarmSystemStateList = new ArrayList<>();
    if(mongoTemplate.find(query, AlarmSystemState.class).size() > 0)
      alarmSystemStateList.add(mongoTemplate.find(query, AlarmSystemState.class).get(0));
    return alarmSystemStateList;
  }

  @Override
  public void stopAlarm(Integer id) {
    AlarmSystemState alarmSystemState = new AlarmSystemState();
    // When the alarmSystemState goes off and we want to stop it, we set the alarmOn state to false and disarm the alarmSystemState
    alarmSystemState.set_id(id);
    alarmSystemState.setAlarmOn(false);
    alarmSystemState.setAlarmState(0);
    alarmSystemState.setVerificationActivated(false);
    alarmSystemStateRepository.save(alarmSystemState);
  }
}
