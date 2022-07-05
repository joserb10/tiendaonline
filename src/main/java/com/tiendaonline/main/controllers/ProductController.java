package com.tiendaonline.main.controllers;

import com.tiendaonline.main.entities.Product;
import com.tiendaonline.main.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

//Api Rest
@RestController
//Direccion base de las peticiones
@RequestMapping(value = "/products")
public class ProductController {
    //Inyeccion dependencia service
    @Autowired
    private ProductService productService;

    //Allow CrossOrigin a todos los clientes
    @CrossOrigin(origins = "*")
    //Petición Get con ruta base /products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProductsByCategory(@Param("category") Integer category) {
        List<Product> products = new ArrayList<>();
        //Validar que se envíe el parametro
        if (category != null) {
            //Obtener todos los productos desde la base de datos
            products = productService.findAllByCategory(category);
        } else {
            //Obtener todos los productos desde la base de datos
            products = productService.findAll();
        }

        //Retornar un ResponseEnitity con los productos y el HttpStatus 200 al cliente
        return new ResponseEntity<List<Product>>(products,null, HttpStatus.OK);
    }


}
