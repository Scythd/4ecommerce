package com.moklyak.ecommerce.repositories;

import com.moklyak.ecommerce.entities.Library;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryRepository extends JpaRepository<Library, Long> {

    Library findByUser_Email(String email);
}
