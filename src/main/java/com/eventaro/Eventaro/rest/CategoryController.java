package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.domain.model.Category;
import com.eventaro.Eventaro.persistence.CategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository _categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        _categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return _categoryRepository.findAll();
    }
}
