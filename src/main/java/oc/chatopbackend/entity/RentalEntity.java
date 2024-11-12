package oc.chatopbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "rentals")
@EntityListeners(AuditingEntityListener.class) // Pour les champs created_at et updated_at
public class RentalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private Integer surface;

    @Column
    private Integer price;

    @Column
    private String picture; //local path from server folder assets

    @Column(length = 1000)
    private String description;

    @Column(name = "owner_id")
    private Integer ownerId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated_at;

}
