package com.tiendaonline.main.crons;

import com.tiendaonline.main.entities.Category;
import com.tiendaonline.main.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestScheduler {
    /*Inyectar dependencia del service de categorias*/
    @Autowired
    private CategoryService categoryService;

    /*Tarea programada para ejecutar una peticion a la base de datos cada 4250 milisegundos*/
    @Async
    @Scheduled(fixedRate = 4250)
    public List<Category> requestDBCategory() {
        //Devolver todas las categorias
        return categoryService.findAll();
    }
}
