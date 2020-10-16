package com.interactivehome.main_service.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisteredPersonDto {
  private String name;
  @JsonProperty("rfid_card_id")
  private String rfidCardId;
  private String password;
}
