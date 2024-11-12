package oc.chatopbackend.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class RentalModel {
    Integer id;
    String name;
    Integer surface;
    Integer price;
    String picture; //local path from server folder assets
    String description;
    Long ownerId;
    String created_at;
    String updated_at;

    public RentalModel(Integer id, String name, Integer surface, Integer price, String picture, String description,
            Long ownerId,
            LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.name = name;
        this.surface = surface;
        this.price = price;
        this.picture = picture;
        this.description = description;
        this.ownerId = ownerId;
        this.created_at = String.valueOf(created_at);
        this.updated_at = String.valueOf(updated_at);
    }

    @Override
    public String toString() {
        return ("Rental : {id:" + id + ", name:" + name + ", surface:" + surface + ", price:" + price + ", picture:" + picture + ", description:" + description + ", ownerId:" + ownerId + ", created_at:" + created_at + ", updated_at:" + updated_at + "}");
    }
}
