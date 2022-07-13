package com.tiendaonline.main.controllers;

import com.tiendaonline.main.entities.Product;
import com.tiendaonline.main.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/products")
public class ProductController {
    //Inyeccion dependencia service
    @Autowired
    private ProductService productService;

    //Petición Get con ruta base /products con paginacion de 8 elementos por paginaAllow y CrossOrigin
    @CrossOrigin(origins = "*")
    @GetMapping
    public ResponseEntity<?> getAllProductsByFilter(@Nullable @RequestParam("category") Integer category,
                                                    @Nullable @RequestParam("text") String text,
                                                    @Nullable @RequestParam("minPrice") Integer minPrice,
                                                    @Nullable @RequestParam("maxPrice") Integer maxPrice,
                                                                  @RequestParam(defaultValue = "0") Integer pageNo,
                                                                  @RequestParam(defaultValue = "8") Integer pageSize,
                                                                  @RequestParam(defaultValue = "id") String sortBy) {
        List<Product> products = new ArrayList<>();
        Page<Product> pagedResult;
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        //Validar que se envíe el parametro category o text o min price
        if (category != null) {
            pagedResult = productService.findAllByCategory(category,paging);
        } else if (text != null){
            pagedResult =  productService.findAllByText(text,paging);
        } else if (minPrice != null) {
            pagedResult =  productService.findAllByPriceRange(minPrice,maxPrice,paging);
        } else {
            pagedResult =  productService.findAll(paging);
        }

        //Si pagedReult contiene datos almacenarlo en array list products
        if(pagedResult.hasContent()) {
            products = pagedResult.getContent();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("currentPage", pagedResult.getNumber());
        response.put("totalItems", pagedResult.getTotalElements());
        response.put("totalPages", pagedResult.getTotalPages());

        return new ResponseEntity<>(response,null, HttpStatus.OK);
    }

}
