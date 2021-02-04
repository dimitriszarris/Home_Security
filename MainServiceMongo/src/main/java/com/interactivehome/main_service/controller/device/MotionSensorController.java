package com.interactivehome.main_service.controller.device;

import com.interactivehome.main_service.model.device.dto.MotionSensorDto;
import com.interactivehome.main_service.model.device.entity.MotionSensor;
import com.interactivehome.main_service.service.device.MotionSensorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class MotionSensorController {

    private final MotionSensorService motionSensorService;

    public MotionSensorController(MotionSensorService motionSensorService) {
        this.motionSensorService = motionSensorService;
    }

    @PostMapping("/motion_sensor")
    public void registerMotionSensor(@RequestBody MotionSensorDto dto,
                                     @RequestParam(value = "alarmId") Integer alarmId) {
        motionSensorService.registerMotionSensor(dto, alarmId);
    }

    @PutMapping("/motion_sensor/{id}")
    public MotionSensor modifyMotionSensorByAlarmIdAndId(@PathVariable Integer id,
                                                         @RequestParam(value = "alarmId") Integer alarmId,
                                                         @RequestBody MotionSensorDto dto) {
        return motionSensorService.modifyMotionSensorByAlarmIdAndId(alarmId, id, dto);
    }

    @GetMapping("/motion_sensor/{id}")
    public MotionSensor getMotionSensorByAlarmIdAndId(@PathVariable Integer id,
                                                      @RequestParam(value = "alarmId") Integer alarmId) {
        return motionSensorService.getMotionSensorByAlarmIdAndId(alarmId, id);
    }

    @GetMapping("/motion_sensor")
    public List<MotionSensor> getAllMotionSensorsByAlarmId( @RequestParam(value = "alarmId") Integer alarmId) {
        return motionSensorService.getAllMotionSensorsByAlarmId(alarmId);
    }

    @DeleteMapping("/motion_sensor/{id}")
    public void deleteMotionSensorByAlarmIdAndId(@PathVariable Integer id,
                                                 @RequestParam(value = "alarmId") Integer alarmId) {
        motionSensorService.deleteMotionSensor(alarmId, id);
    }
}
