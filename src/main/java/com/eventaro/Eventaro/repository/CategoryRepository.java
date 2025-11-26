package com.eventaro.Eventaro.repository;

import com.eventaro.Eventaro.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Hilft uns beim Filtern
    Category findByName(String name);
}