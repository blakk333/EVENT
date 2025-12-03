package com.eventaro.Eventaro.application.category.mapper;

import com.eventaro.Eventaro.application.category.dto.CategoryRequest;
import com.eventaro.Eventaro.application.category.dto.CategoryResponseDTO;
import com.eventaro.Eventaro.domain.category.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponseDTO toResponse(Category entity) {
        if (entity == null) return null;

        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setIcon(entity.getIcon());
        return dto;
    }

    public Category toEntity(CategoryRequest request) {
        if (request == null) return null;

        return new Category(request.getName(), request.getIcon());
    }
}