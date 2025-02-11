package com.dataquadinc.dto;

import com.dataquadinc.model.UserType;  // Correct import
import lombok.Data;

import java.time.LocalDateTime;

@Data

public class LoginResponseDTO {
    private boolean success;
    private String message;
    private Payload payload;
    private ErrorDetails error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public ErrorDetails getError() {
        return error;
    }

    public void setError(ErrorDetails error) {
        this.error = error;
    }

    @Data

    public static class Payload {
        private String userId;
        private UserType roleType;  // Usage of UserType
        private LocalDateTime loginTimestamp;

        // Add a constructor to accept these parameters
        public Payload(String userId, UserType roleType, LocalDateTime loginTimestamp) {
            this.userId = userId;
            this.roleType = roleType;
            this.loginTimestamp = loginTimestamp;
        }
    }

    @Data

    public static class ErrorDetails {
        private String errorCode;
        private String errorMessage;

        public ErrorDetails(String errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }

    public LoginResponseDTO(boolean success, String message, Payload payload) {
        this.success = success;
        this.message = message;
        this.payload = payload;
        this.error = null;
    }

    public LoginResponseDTO(boolean success, String message, Payload payload, ErrorDetails error) {
        this.success = success;
        this.message = message;
        this.payload = payload;
        this.error = error;
    }
}
