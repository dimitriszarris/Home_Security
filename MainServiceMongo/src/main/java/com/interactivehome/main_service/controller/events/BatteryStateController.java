package com.interactivehome.main_service.controller.events;

import com.interactivehome.main_service.model.common.dto.ResponseDto;
import com.interactivehome.main_service.model.events.dto.BatteryStateDto;
import com.interactivehome.main_service.model.events.entity.BatteryState;
import com.interactivehome.main_service.service.events.BatteryStateService;
import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
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
public class BatteryStateController {
  private final BatteryStateService batteryStateService;

  public BatteryStateController(BatteryStateService batteryStateService) {
    this.batteryStateService = batteryStateService;
  }

  @PostMapping("/battery_state/{id}")
  public ResponseEntity<ResponseDto> postBatteryVoltageById(@PathVariable Integer id,
                                                            @RequestParam(value = "alarmId") Integer alarmId,
                                                            @RequestBody BatteryStateDto batteryStateDto) {
    batteryStateService.saveVoltageBySensorId(id, alarmId, batteryStateDto);
    ResponseDto dto = new ResponseDto();
    dto.setSuccess(true);
    return ResponseEntity.ok(dto);
  }

  @GetMapping("/battery_state/{id}")
  public List<BatteryState> getBatteryVoltageByBatteryIdFromDateToDate(
      @PathVariable Integer id,
      @RequestParam(value = "alarmId") Integer alarmId,
      @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
      @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate)
  {
    return batteryStateService.getVoltageByAlarmIdAndSensorIdFromDateToDate(alarmId, id, fromDate, toDate);
  }
}
