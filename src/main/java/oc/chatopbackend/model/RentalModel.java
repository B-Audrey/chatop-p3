package oc.chatopbackend.model;

import lombok.Data;

@Data
public class RentalModel {
    private Long id;
    private String name;
    private Integer surface;
    private Integer price;
    private String picture; //local path from server folder assets
    private String description;
    private Long ownerId;
    private String created_at;
    private String updated_at;

    public RentalModel() {
    }

    @Override
    public String toString() {
        return ("Rental : {id:" + id + ", name:" + name + ", surface:" + surface + ", price:" + price
                + ", picture:" + picture + ", description:" + description + ", ownerId:" + ownerId
                + ", created_at:" + created_at + ", updated_at:" + updated_at + "}");
    }
}
