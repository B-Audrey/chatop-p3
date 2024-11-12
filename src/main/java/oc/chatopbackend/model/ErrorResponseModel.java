package oc.chatopbackend.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponseModel {

    private final LocalDateTime timestamp;
    private final int status;
    private final String message;

    public ErrorResponseModel(int status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }

}
