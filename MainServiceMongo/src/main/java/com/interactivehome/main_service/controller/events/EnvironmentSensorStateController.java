package com.interactivehome.main_service.controller.events;

import com.interactivehome.main_service.model.common.dto.ResponseDto;
import com.interactivehome.main_service.model.events.dto.EnvironmentSensorStateDto;
import com.interactivehome.main_service.model.events.entity.EnvironmentSensorLastMeasurement;
import com.interactivehome.main_service.model.events.entity.EnvironmentSensorState;
import com.interactivehome.main_service.service.events.EnvironmentSensorLastMeasurementsService;
import com.interactivehome.main_service.service.events.EnvironmentSensorStateService;
import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class EnvironmentSensorStateController {
  private final EnvironmentSensorStateService environmentSensorStateService;
  private final EnvironmentSensorLastMeasurementsService environmentSensorLastMeasurementsService;

  public EnvironmentSensorStateController(EnvironmentSensorStateService environmentSensorStateService, EnvironmentSensorLastMeasurementsService environmentSensorLastMeasurementsService) {
    this.environmentSensorStateService = environmentSensorStateService;
    this.environmentSensorLastMeasurementsService = environmentSensorLastMeasurementsService;
  }

  @PostMapping("/environment_sensor_state/{id}")
  public ResponseEntity<ResponseDto> postValues(@PathVariable Integer id,
                                                @RequestParam(value = "alarmId") Integer alarmId,
                                                @RequestBody EnvironmentSensorStateDto dto) {
    environmentSensorStateService.saveValuesBySensorId(id, alarmId, dto);
    ResponseDto responseDto = new ResponseDto();
    responseDto.setSuccess(true);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }
  
  @GetMapping("/environment_sensor_state/{id}")
  public List<EnvironmentSensorStateDto> getEnvironmentSensorValuesByAlarmIdAndSensorIdFromDateToDate(
      @PathVariable Integer id,
      @RequestParam(value = "alarmId") Integer alarmId,
      @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
      @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate)
  {
    return environmentSensorStateService.getValuesByAlarmIdAndSensorIdFromDateToDate(alarmId, id, fromDate, toDate);
  }

  @GetMapping("/environment_sensor_state")
  public List<EnvironmentSensorLastMeasurement> getLastMeasurementsFromAllEnvironmentSensorsByAlarmId(@RequestParam(value = "alarmId") Integer alarmId) {
    return environmentSensorLastMeasurementsService.getLastMeasurementsFromAllEnvironmentSensorsByAlarmId(alarmId);
  }
}
