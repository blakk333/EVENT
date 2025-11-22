package com.eventaro.Eventaro.config;

import com.eventaro.Eventaro.domain.model.Category;
import com.eventaro.Eventaro.domain.model.Organizer;
import com.eventaro.Eventaro.domain.model.User;
import com.eventaro.Eventaro.enums.UserRole;
import com.eventaro.Eventaro.persistence.CategoryRepository;
import com.eventaro.Eventaro.persistence.OrganizerRepository;
import com.eventaro.Eventaro.persistence.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository,
                               CategoryRepository categoryRepository,
                               OrganizerRepository organizerRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Admin User
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
                System.out.println("ADMIN USER CREATED: Username 'admin', Password 'admin123'");
            }

            // 2. Front Office User (für den Empfang)
            if (userRepository.findByUsername("front").isEmpty()) {
                User front = new User();
                front.setUsername("front");
                front.setPassword(passwordEncoder.encode("front123"));
                front.setRole(UserRole.FRONT_OFFICE);
                userRepository.save(front);
                System.out.println("FRONT OFFICE USER CREATED: Username 'front', Password 'front123'");
            }

            // 3. Kategorien erstellen
            if (categoryRepository.count() == 0) {
                createCategory(categoryRepository, "Canyoning", "Exciting tours through gorges");
                createCategory(categoryRepository, "Hiking", "Guided mountain hikes");
                createCategory(categoryRepository, "Skiing", "Winter sports and courses");
                createCategory(categoryRepository, "Climbing", "Indoor and outdoor climbing");
                System.out.println("CATEGORIES CREATED");
            }

            // 4. Organisatoren erstellen
            if (organizerRepository.count() == 0) {
                createOrganizer(organizerRepository, "Alpine Adventures", "contact@alpine.com");
                createOrganizer(organizerRepository, "Ski School Tyrol", "info@skityrol.at");
                createOrganizer(organizerRepository, "Outdoor Fun GmbH", "office@outdoorfun.com");
                System.out.println("ORGANIZERS CREATED");
            }
        };
    }

    private void createCategory(CategoryRepository repo, String name, String description) {
        Category c = new Category();
        c.setName(name);
        c.setDescription(description);
        repo.save(c);
    }

    private void createOrganizer(OrganizerRepository repo, String name, String email) {
        Organizer o = new Organizer();
        o.setOrganizationName(name);
        o.setEmail(email);
        o.setContactPerson("Max Mustermann");
        o.setPhone("+43 123 45678");
        repo.save(o);
    }
}