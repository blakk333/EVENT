package com.eventaro.Eventaro.presentation.rest;

import com.eventaro.Eventaro.application.category.CategoryService;
import com.eventaro.Eventaro.application.category.dto.CategoryRequest; // NEU
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Liste aller Kategorien (Back Office)
    @GetMapping("/manage")
    public String manageCategories(Model model) {
        // Der Service liefert jetzt DTOs, das Template zeigt diese an (Feldnamen sind identisch)
        model.addAttribute("categories", categoryService.getAllCategories());
        return "management/categories-list";
    }

    // Formular für neue Kategorie
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        // Wir binden das Formular an das Request DTO
        model.addAttribute("category", new CategoryRequest());
        return "management/category-form";
    }

    // Speichern
    @PostMapping("/save")
    public String saveCategory(@ModelAttribute CategoryRequest categoryRequest) {
        // Service nimmt jetzt das DTO
        categoryService.createCategory(categoryRequest);
        return "redirect:/categories/manage";
    }

    // Löschen
    @PostMapping("/delete")
    public String deleteCategory(@RequestParam Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories/manage";
    }
}