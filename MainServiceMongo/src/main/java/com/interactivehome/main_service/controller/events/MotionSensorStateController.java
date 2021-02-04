package com.interactivehome.main_service.controller.events;

import com.interactivehome.main_service.model.common.dto.ResponseDto;
import com.interactivehome.main_service.model.device.entity.AlarmSystem;
import com.interactivehome.main_service.model.events.dto.MotionSensorStateDto;
import com.interactivehome.main_service.model.events.entity.AlarmSystemState;
import com.interactivehome.main_service.model.events.entity.MotionSensorState;
import com.interactivehome.main_service.service.events.AlarmSystemStateService;
import com.interactivehome.main_service.service.events.MotionSensorStateService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.client.RestTemplate;


@CrossOrigin(origins = "*")
@RestController
public class MotionSensorStateController {

  private final MotionSensorStateService motionSensorStateService;

  @Autowired
  private AlarmSystemStateService alarmSystemStateService;

  MotionSensorStateController(MotionSensorStateService motionSensorStateService) {
    this.motionSensorStateService = motionSensorStateService;
  }

  @PostMapping("/motion_sensor_state/{id}")
  public ResponseEntity<ResponseDto> postMovementSensorState(@PathVariable Integer id,
                                                             @RequestParam(value = "alarmId") Integer alarmId,
                                                             @RequestBody MotionSensorStateDto dto) {
    List<AlarmSystemState> alarmSystemState = alarmSystemStateService.getAlarmStateByIdFromDateToDate(alarmId, null, null);
    if(alarmSystemState == null) {
      ResponseDto responseDto = new ResponseDto();
      responseDto.setSuccess(false);
      responseDto.setMessage("The alarm is not set");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
    }
    System.out.println("AlarmSystemState state is : " + alarmSystemState.toString());
    if(alarmSystemState.get(0).getAlarmState() == 0) {
      System.out.println("The alarm is deactivated.");
      ResponseDto responseDto = new ResponseDto();
      responseDto.setSuccess(true);
      responseDto.setMessage("The alarm is deactivated.");
      return ResponseEntity.ok(responseDto);
    }
    motionSensorStateService.saveStateBySensorId(id, alarmId, dto);
    ResponseDto responseDto = new ResponseDto();
    responseDto.setSuccess(true);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  @GetMapping("motion_sensor_state/{id}")
  public List<MotionSensorState> getSensorActivityByAlarmIdAndSensorIdFromDateToDate(
      @PathVariable Integer id,
      @RequestParam(value = "alarmId") Integer alarmId,
      @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
      @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate) {

    return motionSensorStateService.getSensorActivityByAlarmIdAndSensorId(alarmId, id, fromDate, toDate);
  }
}
