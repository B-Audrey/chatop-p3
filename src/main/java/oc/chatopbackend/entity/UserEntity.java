package oc.chatopbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Setter
@Getter
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class) // Pour les champs created_at et updated_at
public class UserEntity implements UserDetails {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, insertable = false)
    private LocalDateTime updated_at;

    public UserEntity() {
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // unused for now, no roles, everyone is user
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return email; // use Email as name to find user
    }
}

