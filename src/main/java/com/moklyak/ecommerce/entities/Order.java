package com.moklyak.ecommerce.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id()
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Timestamp dateProvided;

    @ManyToMany(targetEntity = Item.class)
    @JoinTable(name = "cart_items",
            joinColumns = {@JoinColumn(name = "cart_id")},
            inverseJoinColumns = {@JoinColumn(name = "item_id")})
    private List<Item> items;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User user;


}
