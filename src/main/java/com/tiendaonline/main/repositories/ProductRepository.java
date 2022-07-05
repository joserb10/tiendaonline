package com.tiendaonline.main.repositories;

import com.tiendaonline.main.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
