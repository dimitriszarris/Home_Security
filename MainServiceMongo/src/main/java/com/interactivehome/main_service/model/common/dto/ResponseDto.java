package com.interactivehome.main_service.model.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto {
    private Boolean success;
    private Integer deviceId;
    private String message;

    public ResponseDto() {

    }
}
