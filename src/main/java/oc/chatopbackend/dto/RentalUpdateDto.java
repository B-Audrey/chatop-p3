package oc.chatopbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RentalUpdateDto {
    @NotNull(message = "Name is required")
    @Size(min = 2, message = "Name must be between 3 and 500 characters")
    private String name;

    @NotNull(message = "Surface is required")
    private Integer surface;

    @NotNull(message = "Price is required")
    private Integer price;

    @Size(min = 2, message = "Description must have minimum 2 characters")
    private String description;
}
