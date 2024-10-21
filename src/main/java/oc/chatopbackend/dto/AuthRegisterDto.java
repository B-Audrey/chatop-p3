package oc.chatopbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRegisterDto {
    @Email()
    @NotEmpty()
    @NotBlank()
    private String email;

    @NotEmpty()
    @NotBlank()
    @Size(min = 2)
    private String name;

    @NotEmpty()
    @NotBlank()
    @Size(min = 6)
    private String password;

}
