package com.moklyak.ecommerce.repositories;

import com.moklyak.ecommerce.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
