package com.interactivehome.main_service.controller.device;

import com.interactivehome.main_service.model.device.dto.EnvironmentSensorDto;
import com.interactivehome.main_service.model.device.entity.EnvironmentSensor;
import com.interactivehome.main_service.service.device.EnvironmentSensorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "*")
@RestController
public class EnvironmentSensorController {

    private final EnvironmentSensorService environmentSensorService;

    public EnvironmentSensorController(EnvironmentSensorService environmentSensorService) {
        this.environmentSensorService = environmentSensorService;
    }

    @PostMapping("/environment_sensor")
    public Integer registerEnvironmentSensor(@RequestBody EnvironmentSensorDto dto,
                                             @RequestParam(value = "alarmId") Integer alarmId) {
        return environmentSensorService.registerEnvironmentSensor(dto, alarmId);
    }

    @PutMapping("/environment_sensor/{id}")
    public void modifyEnvironmentSensorByAlarmIdAndId(@PathVariable Integer id,
                                                      @RequestParam(value = "alarmId") Integer alarmId,
                                                      @RequestBody EnvironmentSensorDto dto) {
        environmentSensorService.modifyEnvironmentSensorByAlarmIdAndId(alarmId, id, dto);
    }

    @GetMapping("/environment_sensor/{id}")
    public EnvironmentSensor getEnvironmentSensorByAlarmIdAndId(@PathVariable Integer id,
                                                                @RequestParam(value = "alarmId") Integer alarmId) {
        return environmentSensorService.getEnvironmentSensorByAlarmIdAndId(alarmId, id);
    }

    @GetMapping("/environment_sensor")
    public List<EnvironmentSensor> getAllEnvironmentSensorsByAlarmId( @RequestParam(value = "alarmId") Integer alarmId) {
        return environmentSensorService.getAllEnvironmentSensorsByAlarmId(alarmId);
    }

    @DeleteMapping("/environment_sensor/{id}")
    public void deleteEnvironmentSensorByAlarmIdAndId(@PathVariable Integer id,
                                                      @RequestParam(value = "alarmId") Integer alarmId) {
        environmentSensorService.deleteEnvironmentSensorByAlarmIdAndId(alarmId, id);
    }
}
