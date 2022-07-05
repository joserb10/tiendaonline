package com.tiendaonline.main.services;

import com.tiendaonline.main.entities.Product;
import com.tiendaonline.main.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    //Inyeccion de dependencia repositorio
    @Autowired
    ProductRepository productRepository;

    //Obtener todas los productos
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    //Obtener todos los productos por categoria
    public List<Product> findAllByCategory(Integer category) {
        return productRepository.findByCategory(category);
    }
}