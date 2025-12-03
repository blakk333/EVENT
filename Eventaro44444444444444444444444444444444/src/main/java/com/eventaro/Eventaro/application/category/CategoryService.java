package com.eventaro.Eventaro.application.category;

import com.eventaro.Eventaro.application.category.dto.CategoryRequest;
import com.eventaro.Eventaro.application.category.dto.CategoryResponseDTO;
import com.eventaro.Eventaro.application.category.mapper.CategoryMapper;
import com.eventaro.Eventaro.domain.category.Category;
import com.eventaro.Eventaro.domain.category.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper mapper;

    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createCategory(CategoryRequest request) {
        Category category = mapper.toEntity(request);
        categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public Optional<CategoryResponseDTO> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(mapper::toResponse);
    }
}