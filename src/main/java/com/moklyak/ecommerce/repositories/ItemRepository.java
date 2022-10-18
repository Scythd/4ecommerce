package com.moklyak.ecommerce.repositories;

import com.moklyak.ecommerce.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
