package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
