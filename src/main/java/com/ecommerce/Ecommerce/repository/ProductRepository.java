package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
