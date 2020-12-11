package com.interactivehome.main_service.controller.events;


import com.interactivehome.main_service.model.events.dto.DoorSensorStateDto;
import com.interactivehome.main_service.model.events.entity.AlarmSystemState;
import com.interactivehome.main_service.model.events.entity.DoorSensorState;
import com.interactivehome.main_service.service.events.AlarmSystemStateService;
import com.interactivehome.main_service.service.events.DoorSensorStateService;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "*")
@RestController
public class DoorSensorStateController {

    private final DoorSensorStateService doorSensorStateService;

    @Autowired
    private AlarmSystemStateService alarmSystemStateService;

    @Value("$(securityControllerIpPort)")
    private String securityControllerIpPort;
    @Value("$(verificationProcessEndpoint)")
    private String verificationProcessEndpoint;
    @Value("$(verification_process_timeout_sec)")
    private String verificationProcessTimeoutSec;

    private RestTemplate restTemplate;

    public DoorSensorStateController(DoorSensorStateService doorSensorStateService,
                                     RestTemplate restTemplate) {
        this.doorSensorStateService = doorSensorStateService;
        this.restTemplate = restTemplate;
    }


    @PostMapping("/door_sensor_state/{id}")
    public ResponseEntity<String> postDoorState(@PathVariable Integer id,
                                                @RequestBody DoorSensorStateDto dto) {

        List<AlarmSystemState> alarmSystemState = alarmSystemStateService.getAlarmStateByIdFromDateToDate(dto.alarmId, null, null);
        if(alarmSystemState == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("The alarm is not set");
        System.out.println("AlarmSystemState state is : " + alarmSystemState.toString());
        if(alarmSystemState.get(0).getAlarmState() == 0) {
            System.out.println("The alarm is deactivated.");
            return ResponseEntity.ok("200");
        }

        return doorSensorStateService.saveStateByDoorSensorId(id, alarmSystemState.get(0).getAlarmState(), dto);
    }

    @GetMapping("/door_sensor_state/{id}")
    public List<DoorSensorState> getDoorStateByAlarmIdAndSensorIdFromDateToDate(
            @PathVariable Integer id,
            @RequestParam(value = "alarmId") Integer alarmId,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate)
    {
        return doorSensorStateService.getDoorStateByAlarmIdAndSensorIdFromDateToDate(alarmId, id, fromDate, toDate);
    }
}
