package com.interactivehome.main_service.controller.events;

import com.interactivehome.main_service.config.AppProperties;
import com.interactivehome.main_service.model.common.dto.ResponseDto;
import com.interactivehome.main_service.model.events.dto.AlarmSystemStateDto;
import com.interactivehome.main_service.model.events.entity.AlarmSystemState;
import com.interactivehome.main_service.service.events.AlarmSystemStateService;
import io.micrometer.core.instrument.util.StringEscapeUtils;
import java.util.Date;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "*")
@RestController
public class AlarmSystemStateController {
  private final AlarmSystemStateService alarmSystemStateService;
  private final RestTemplate restTemplate;

  @Autowired
  private AppProperties appProperties;

  @Value("$(securityControllerIpPort)")
  private String securityControllerIpPort;
  @Value("$(alarmStateEndpoint)")
  private String alarmStateEndpoint;
  @Value("$(stopAlarmEndpoint)")
  private String stopAlarmEndpoint;

  public AlarmSystemStateController(AlarmSystemStateService alarmSystemStateService,
                                    RestTemplate restTemplate) {
    this.alarmSystemStateService = alarmSystemStateService;
    this.restTemplate = restTemplate;
  }

  @PostMapping("/alarm_system_state/{id}")
  public ResponseEntity<ResponseDto> postAlarmStateById(@PathVariable Integer id,
                                                        @RequestBody AlarmSystemStateDto dto) {
    System.out.println("Got request for postAlarmState: " + dto.toString());
    alarmSystemStateService.saveAlarmStateById(id, dto);
    ResponseDto responseDto = new ResponseDto();
    responseDto.setSuccess(true);
    return ResponseEntity.ok(responseDto);
/*    try {
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
        alarmSystemStateService.saveAlarmStateById(id, dto);
        return ResponseEntity.ok("200");
      }
      else
      {
        System.out.println("Error response from security controller while trying to update the alarm state. " +
            responseEntity.getStatusCode() + ": " + responseEntity.getBody());
        return responseEntity;
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
      return new ResponseEntity<>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
    }
*/
  }

  @GetMapping("/alarm_system_state/{id}")
  public List<AlarmSystemState> getAlarmStateByIdFromDateToDate(
      @PathVariable Integer id,
      @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
      @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate)
  {
    return alarmSystemStateService.getAlarmStateByIdFromDateToDate(id, fromDate, toDate);
  }

  @PostMapping("/stop_alarm/{id}")
  public ResponseEntity postStopAlarm(@PathVariable Integer id,
                                      @RequestBody AlarmSystemStateDto dto) {

    // First stop the alarm at the security controller
    try {
      System.out.println("Stop alarm request address: " + appProperties.getSecurityControllerIpPort() + appProperties.getAlarmStateEndpoint());
      HttpHeaders requestHeaders = new HttpHeaders();
      requestHeaders.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);
      ResponseEntity<String> responseEntity =
          restTemplate.exchange(
              appProperties.getSecurityControllerIpPort() + appProperties.getStopAlarmEndpoint(),
              HttpMethod.GET,
              requestEntity,
              String.class);

      System.out.println("Get stop alarm " + responseEntity.getStatusCode().toString());
      if(responseEntity.getStatusCode() == HttpStatus.OK)
      {
        System.out.println("The alarm stopped successfully in the security controller");
        System.out.println("Saving stop alarm");
        //alarmSystemStateService.stopAlarm(dto);
        postAlarmStateById(id, dto);
        return ResponseEntity.ok("200");
      }
      else
      {
        System.out.println("Error response from security controller while trying to update the alarm state. " +
            responseEntity.getStatusCode() + ": " + responseEntity.getBody());
        return responseEntity;
      }


    }
    catch (RuntimeException exception) {
      System.out.println("Runtime exception: " + exception.getMessage());
      JSONObject payload = new JSONObject();
      try {
        payload.put("message", "The security controller alarm state could not get updated!");
        payload.put("error", StringEscapeUtils.escapeJson(exception.toString()));
      } catch (JSONException e) {
        System.out.println(e.toString());
      }
      return new ResponseEntity<>(payload.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
