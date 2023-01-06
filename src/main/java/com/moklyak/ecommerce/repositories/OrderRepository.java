package com.moklyak.ecommerce.repositories;

import com.moklyak.ecommerce.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
