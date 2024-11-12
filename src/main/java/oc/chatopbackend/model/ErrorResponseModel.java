package oc.chatopbackend.model;

import java.time.LocalDateTime;

public class ErrorResponseModel {

    private final LocalDateTime timestamp;
    private final int status;
    private final String message;

    public ErrorResponseModel(int status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
