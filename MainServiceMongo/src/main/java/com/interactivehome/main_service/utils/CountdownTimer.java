package com.interactivehome.main_service.utils;

import com.interactivehome.main_service.model.dto.AlarmDto;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class CountdownTimer extends Timer
{
    private final RestTemplate restTemplate;

    public CountdownTimer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private enum timer_state {
        started,
        expired,
        stopped
    }

    private timer_state timerState;

    class TimerExpired extends TimerTask {
        public void run() {
            verificationTimerExpired();
        }
    }

    public void verificationTimerStart(int seconds) {
        schedule(new TimerExpired(),seconds * 1000);
        timerState = timer_state.started;
        System.out.println("The verification timer is initiated. Timeout = " + seconds + " sec.");
    }

    public void verificationTimerStop() {
        if(timerState == timer_state.started) {
            this.cancel();
            timerState = timer_state.stopped;
        }
    }

    private void verificationTimerExpired() {
        this.cancel();
        timerState = timer_state.expired;
        System.out.println("The verification timer has expired without any person been verified!");
        OnTimerExpired();
    }

    private void OnTimerExpired() {
        System.out.println("OnTimerExpired is called.");
        try {
            String alarmControllerTriggerAlarm = "http://192.168.1.129:8080/trigger_alarm";

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            AlarmDto alarmDTO = new AlarmDto();
            alarmDTO.setAlarmOn(true);
            HttpEntity<AlarmDto> requestUpdate = new HttpEntity<>(alarmDTO, headers);

            ResponseEntity<Void> response =
                restTemplate.exchange(alarmControllerTriggerAlarm, HttpMethod.PUT, requestUpdate, Void.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                System.out.println("Error response from security controller. " +
                    response.getStatusCode() + ": " + requestUpdate.getBody());
            }
        }
        catch (RuntimeException exception) {
            System.out.println("Runtime exception: " + exception.getMessage());
        }
    }
}
