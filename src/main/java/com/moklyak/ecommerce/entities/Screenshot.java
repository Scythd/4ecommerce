package com.moklyak.ecommerce.entities;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "screenshots")
public class Screenshot {

    @Id()
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private byte[] screenshot;

    @ManyToMany(mappedBy = "screenshots")
    private Item item;
}
