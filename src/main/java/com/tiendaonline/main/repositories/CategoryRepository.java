package com.tiendaonline.main.repositories;

import com.tiendaonline.main.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
