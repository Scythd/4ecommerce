package com.moklyak.ecommerce.entities;


import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity()
@Data()
@Table(name = "users")
public class User {

    @Id()
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "tutorial_tags",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<Role> roles;

    @Column
    private byte[] image;

}
