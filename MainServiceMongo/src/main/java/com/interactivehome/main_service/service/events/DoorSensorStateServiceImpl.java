package com.interactivehome.main_service.service.events;

import com.interactivehome.main_service.config.AppProperties;
import com.interactivehome.main_service.model.common.dto.ResponseDto;
import com.interactivehome.main_service.model.events.dto.AlarmSystemStateDto;
import com.interactivehome.main_service.model.events.dto.DoorSensorStateDto;
import com.interactivehome.main_service.model.events.entity.DoorSensorState;
import com.interactivehome.main_service.repository.events.DoorSensorStateRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.interactivehome.main_service.utils.CountdownTimer;
import io.micrometer.core.instrument.util.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DoorSensorStateServiceImpl implements DoorSensorStateService {

    private final DoorSensorStateRepository doorSensorStateRepository;
    private final MongoTemplate mongoTemplate;
    private final AlarmSystemStateService alarmSystemStateService;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    CountdownTimer countdownTimer;

    @Value("$(verification_process_endpoint)")
    private String verificationProcessEndpoint;
    @Value("$(verification_process_timeout_sec)")
    private String verificationProcessTimeoutSec;

    private RestTemplate restTemplate;

    public DoorSensorStateServiceImpl(DoorSensorStateRepository doorSensorStateRepository,
                                      MongoTemplate mongoTemplate, AlarmSystemStateService alarmSystemStateService) {
        this.doorSensorStateRepository = doorSensorStateRepository;
        this.mongoTemplate = mongoTemplate;
        this.alarmSystemStateService = alarmSystemStateService;
    }

    @Override
public ResponseEntity<ResponseDto> saveStateByDoorSensorId(Integer id, Integer alarmId, Integer alarmState, DoorSensorStateDto dto) {
        DoorSensorState doorSensorState = new DoorSensorState();
        doorSensorState.setSensorId(id);
        doorSensorState.mapFromDto(dto);
        doorSensorState.setAlarmId(alarmId);
        // If we reach at this point, the alarm is armed and the door sensor is enabled and for this alarm state.
        // If the door opens, the verification process should be triggered
        if(dto.getDoorState()) {
            AlarmSystemStateDto alarmSystemStateDto = new AlarmSystemStateDto();
            alarmSystemStateDto.set_id(alarmId);
            alarmSystemStateDto.setAlarmOn(false);
            alarmSystemStateDto.setAlarmState(alarmState);
            alarmSystemStateDto.setVerificationActivated(true);
            alarmSystemStateService.saveAlarmStateById(alarmId, alarmSystemStateDto);
        }

        doorSensorStateRepository.save(doorSensorState);

        // If the door opens and the alarm is armed then trigger the initiation of the verification process
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            System.out.println("Security controller verification process endpoint: " +
                    appProperties.getSecurityControllerIpPort() + appProperties.getVerificationProcessEndpoint());
            ResponseEntity<String> responseEntity =
                    restTemplate.exchange(
                            appProperties.getSecurityControllerIpPort()
                                    + appProperties.getVerificationProcessEndpoint(),
                            HttpMethod.GET,
                            entity,
                            String.class);

            if(responseEntity.getStatusCode() == HttpStatus.OK)
            {
                countdownTimer.verificationTimerStart(alarmId, appProperties.getVerificationProcessTimeoutSec());
                ResponseDto responseDto = new ResponseDto();
                responseDto.setSuccess(true);
                return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
            }
            else
            {
                System.out.println("Error response from security controller. " +
                        responseEntity.getStatusCode() + ": " + responseEntity.getBody());
            }

            ResponseDto responseDto = new ResponseDto();
            responseDto.setSuccess(true);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }
        catch (RuntimeException exception) {
            ResponseDto responseDto = new ResponseDto();
            responseDto.setSuccess(false);
            responseDto.setMessage("The door opened but the verification process could not get started!" + "Exception: " +
                    StringEscapeUtils.escapeJson(exception.toString()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseDto);
        }
    }

    @Override
    public List<DoorSensorState> getDoorStateByAlarmIdAndSensorIdFromDateToDate(Integer alarmId, Integer sensorId, Date fromDate, Date toDate) {
        if(fromDate != null && !fromDate.toString().isEmpty() && (toDate == null || toDate.toString().isEmpty())) {
            toDate = java.util.Date.from(LocalDate.now().atStartOfDay().plusDays(1)
                .atZone(ZoneId.systemDefault())
                .toInstant());
        }

        String messageOut;
        messageOut = "Get door state by alarm id: " + alarmId + " and door id: " + sensorId;
        if(fromDate != null && toDate != null)
            messageOut += " from date: " + fromDate.toString() + ", to date : " + toDate.toString();
        System.out.println(messageOut);

        Query query = new Query();
        query.addCriteria(Criteria.where("alarm_id").is(alarmId));
        query.addCriteria(Criteria.where("sensor_id").is(sensorId));
        if((fromDate != null && toDate != null) && (!fromDate.toString().isEmpty() && !toDate.toString().isEmpty())) {
            query.addCriteria(Criteria.where("updated_utc").gte(fromDate).lte(toDate));
            query.with(new org.springframework.data.domain.Sort(Direction.DESC, "updated_utc"));
            return mongoTemplate.find(query, DoorSensorState.class);
        }
        // If the dates are not present then get the latest voltage measurement
        query.with(new org.springframework.data.domain.Sort(Direction.DESC, "updated_utc"));
        List<DoorSensorState> doorSensorStateList = new ArrayList<>();
        if(mongoTemplate.find(query, DoorSensorState.class).size() > 0)
            doorSensorStateList.add(mongoTemplate.find(query, DoorSensorState.class).get(0));
        return doorSensorStateList;
    }
}
