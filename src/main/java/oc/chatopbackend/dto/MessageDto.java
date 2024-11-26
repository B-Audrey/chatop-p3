package oc.chatopbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageDto {

    @NotNull(message = "Message content is required")
    @Size(min = 1, max = 1000, message = "Message must be between 1 and 1000 characters")
    private String message;

    @NotNull(message = "User ID is required")
    private Long user_id;

    @NotNull(message = "Rental ID is required")
    private Long rental_id;
}
