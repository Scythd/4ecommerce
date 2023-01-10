package com.moklyak.ecommerce.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;
import java.util.Objects;


@Entity
@Data
@Table(name = "items")
public class Item {


    @Id()
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany(mappedBy = "items")
    private List<Cart> cart;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String creator;

    @Column
    private String additionalInfo;

    @ManyToMany(targetEntity = Tag.class)
    @JoinTable(name = "item_tags",
            joinColumns = {@JoinColumn(name = "item_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> tags;

    @Column
    private Date releaseDate;

    @Column
    private String posterURI;

    @Column
    private String fileURI;

    @OneToMany(targetEntity = Screenshot.class)
    @JoinColumn(name = "screenshot_id")
    private List<Screenshot> screenshots;

    @ManyToMany(mappedBy = "items")
    private List<Library> libraries;

    @ManyToMany(mappedBy = "items")
    private List<Order> orders;

    @Column
    private double price;

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +

                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", creator='" + creator + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                ", tags=" + tags +
                ", releaseDate=" + releaseDate +
                ", posterURI='" + posterURI + '\'' +
                ", fileURI='" + fileURI + '\'' +
                ", screenshots=" + screenshots +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
