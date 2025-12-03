package com.eventaro.Eventaro.application.user.mapper;

import com.eventaro.Eventaro.application.user.dto.UserRequest;
import com.eventaro.Eventaro.application.user.dto.UserResponseDTO;
import com.eventaro.Eventaro.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toResponse(User entity) {
        if (entity == null) return null;

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setFullName(entity.getFullName());
        dto.setRole(entity.getRole());
        return dto;
    }

    public User toEntity(UserRequest request) {
        if (request == null) return null;

        // Passwort wird hier noch nicht gehasht, das macht der Service!
        return new User(
                request.getUsername(),
                request.getPassword(),
                request.getRole(),
                request.getFullName()
        );
    }
}