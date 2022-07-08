package com.tiendaonline.main.repositories;

import com.tiendaonline.main.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {
    //Query para obtener productos por categoria previniendo sql inyection con parametro indexado y con paginacion
    @Query(value = "select id,name,url_image,price,discount,category from product where category = ?1",
            countQuery = "select count(*) from product where category = ?1", nativeQuery = true)
    Page<Product> findByCategory(@Param("category") Integer category, Pageable pageable);

    //Query para obtener productos por busqueda de texto previniendo sql inyection con parametro indexado y con paginacion
    @Query(value = "select id,name,url_image,price,discount,category from product where name like CONCAT('%', ?1, '%')",
            countQuery = "select count(*) from product where name like '%?1%'", nativeQuery = true)
    Page<Product> findByText(@Param("text") String text, Pageable pageable);


}
