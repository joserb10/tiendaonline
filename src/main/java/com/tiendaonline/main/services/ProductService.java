package com.tiendaonline.main.services;

import com.tiendaonline.main.entities.Category;
import com.tiendaonline.main.entities.Product;
import com.tiendaonline.main.repositories.CategoryRepository;
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
}
