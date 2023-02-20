package com.sitech.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ErrorResponse {
    @JsonProperty("code")
    private int code;
    @JsonProperty("errorMessage")
    private String errorMessage;

}
