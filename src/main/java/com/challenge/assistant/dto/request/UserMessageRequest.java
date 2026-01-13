package com.challenge.assistant.dto.request;
import jakarta.validation.constraints.NotBlank;

public class UserMessageRequest {

    @NotBlank
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
