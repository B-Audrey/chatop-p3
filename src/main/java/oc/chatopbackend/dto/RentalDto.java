package oc.chatopbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RentalDto {

    private Integer id;

    @NotNull(message = "Name is required")
    @Size(min = 2, message = "Name must be between 3 and 500 characters")
    private String name;

    @NotNull(message = "Surface is required")
    private Integer surface;

    @NotNull(message = "Price is required")
    private Integer price;

    private MultipartFile picture;

    @Size(min = 2, message = "Description must have minimum 2 characters")
    @NotNull(message = "Description is required")
    private String description;

    private String createdAt;
    private String updatedAt;
}
