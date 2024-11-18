package oc.chatopbackend.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserModel {
    private Long id;
    private String email;
    private String name;
    private String created_at;
    private String updated_at;

    public UserModel(Long id, String email, String name, LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.created_at = String.valueOf(created_at);
        this.updated_at = String.valueOf(updated_at);
    }

    @Override
    public String toString() {
        return "User:{id=" + id + ", email=" + email + ", name=" + name + ", created_at=" + created_at + ", " +
                "updated_at=" + updated_at + "}";
    }
}
