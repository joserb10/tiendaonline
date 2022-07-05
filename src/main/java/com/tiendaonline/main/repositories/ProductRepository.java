package com.tiendaonline.main.repositories;

import com.tiendaonline.main.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    //Query para obtener productos previniendo sql inyection con parametro indexado
    @Query(value = "select * from product where category = ?1", nativeQuery = true)
    List<Product> findByCategory(@Param("category") Integer category);
}
