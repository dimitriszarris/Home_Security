package com.interactivehome.main_service.controller.device;

import com.interactivehome.main_service.model.device.dto.DoorSensorDto;
import com.interactivehome.main_service.model.device.entity.DoorSensor;
import com.interactivehome.main_service.service.device.DoorSensorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class DoorSensorController {
    private final DoorSensorService doorSensorService;

    public DoorSensorController(DoorSensorService doorSensorService) {
        this.doorSensorService = doorSensorService;
    }

    @PostMapping("/door_sensor")
    public void registerDoorSensor(@RequestBody DoorSensorDto dto,
                                   @RequestParam(value = "alarmId") Integer alarmId) {
        doorSensorService.registerDoorSensor(dto, alarmId);
    }

    @PutMapping("/door_sensor/{id}")
    public DoorSensor modifyDoorSensorByAlarmIdAndId(@PathVariable Integer id,
                                                     @RequestParam(value = "alarmId") Integer alarmId,
                                                     @RequestBody DoorSensorDto dto) {
        return doorSensorService.modifyDoorSensorByAlarmIdAndId(alarmId, id, dto);
    }

    @GetMapping("/door_sensor/{id}")
    public DoorSensor getDoorSensorByAlarmIdAndId(@PathVariable Integer id,
                                                  @RequestParam(value = "alarmId") Integer alarmId) {
        return doorSensorService.getDoorSensorByAlarmIdAndId(alarmId, id);
    }

    @GetMapping("door_sensor")
    public List<DoorSensor> getAllDoorSensorsByAlarmId( @RequestParam(value = "alarmId") Integer alarmId) {
        return doorSensorService.getAllDoorSensorsByAlarmId(alarmId);
    }

    @DeleteMapping("/door_sensor/{id}")
    public void deleteDoorSensorByAlarmIdAndId(@PathVariable Integer id,
                                               @RequestParam(value = "alarmId") Integer alarmId) {
        doorSensorService.deleteDoorSensorByAlarmIdAndId(alarmId, id);
    }
}
