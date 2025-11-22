package com.eventaro.Eventaro.rest;

import com.eventaro.Eventaro.persistence.CategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categories")
public class CategoryViewController {

    private final CategoryRepository categoryRepository;

    public CategoryViewController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String viewCategories(Model model) {
        // Alle Kategorien laden und ins Model packen
        model.addAttribute("categories", categoryRepository.findAll());
        return "view-categories"; // Sucht nach view-categories.html
    }
}