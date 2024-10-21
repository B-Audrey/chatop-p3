package oc.chatopbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class AuthLoginDto {
    @Email()
    @NotEmpty()
    private String email;

    @NotEmpty()
    @Size(min = 8)
    private String password;

}
