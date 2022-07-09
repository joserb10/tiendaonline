package com.tiendaonline.main.crons;

import com.tiendaonline.main.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RequestScheduler {
    @Autowired
    private CategoryService categoryService;

    @Scheduled(cron = "*/5 * * * * *")
    public void requestDBCategory() {
        categoryService.findAll();
    }
}
