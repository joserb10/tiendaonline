package com.tiendaonline.main.services;

import com.tiendaonline.main.entities.Category;
import com.tiendaonline.main.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    //Inyeccion de dependencia repositorio
    @Autowired
    CategoryRepository categoryRepository;

    //Obtener todas las categorias
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

}
