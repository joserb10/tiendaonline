package com.tiendaonline.main.controllers;

import com.tiendaonline.main.entities.Category;
import com.tiendaonline.main.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "categories")
public class CategoryController {
    //Inyeccion de dependencia service
    @Autowired
    private CategoryService categoryService;

    //Petición Get con ruta base /categories y Allow CrossOrigin
    @CrossOrigin(origins = "*")
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.findAll();

        return new ResponseEntity<List<Category>>(categories,null, HttpStatus.OK);
    }
}
