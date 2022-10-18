package com.moklyak.ecommerce.repositories;

import com.moklyak.ecommerce.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
