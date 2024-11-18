package oc.chatopbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
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
    private String description;

    private String createdAt;
    private String updatedAt;
}
