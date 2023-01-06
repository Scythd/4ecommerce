package com.moklyak.ecommerce.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;


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
    private String studio;

    @Column
    private String aboutGame;

    @ManyToMany(targetEntity = Tag.class)
    @JoinTable(name = "item_tags",
            joinColumns = {@JoinColumn(name = "item_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> tags;

    @Column
    private LocalDate releaseDate;

    @Column
    private byte[] poster;

    @OneToMany(targetEntity = Screenshot.class)
    @JoinColumn(name = "screenshot_id")
    private List<Screenshot> screenshots;

    @ManyToMany(mappedBy = "items")
    private List<Library> libraries;

    @ManyToMany(mappedBy = "items")
    private List<Order> orders;
}
