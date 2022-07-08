package com.tiendaonline.main.services;

import com.tiendaonline.main.entities.Product;
import com.tiendaonline.main.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    //Inyeccion de dependencia repositorio
    @Autowired
    ProductRepository productRepository;

    //Obtener todas los productos
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    //Obtener todos los productos por categoria
    public Page<Product> findAllByCategory(Integer category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    //Obtener todos los productos por texto busqueda
        public Page<Product> findAllByText(String text, Pageable pageable) {
        return productRepository.findByText(text, pageable);
    }
}
