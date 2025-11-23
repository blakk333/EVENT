package com.eventaro.Eventaro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // WICHTIG: Wir entfernen hier den Konstruktor und die Variable für CustomUserDetailsService.
    // Spring findet den Service automatisch, weil er ein @Bean (via @Service) ist.
    // Das verhindert den "Could not postProcess"-Fehler.

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. Statische Ressourcen (CSS, Bilder)
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.ico").permitAll()

                        // 2. Öffentliche Seiten
                        .requestMatchers("/", "/login", "/register", "/error").permitAll()
                        .requestMatchers("/events/details/**", "/events/details/*/image").permitAll()

                        // 3. Backoffice: Nur für Mitarbeiter/Admins
                        .requestMatchers("/dashboard", "/log", "/categories/**", "/organizers/**").hasAnyRole("ADMIN", "FRONT_OFFICE")
                        .requestMatchers("/events/create", "/events/edit/**").hasRole("ADMIN")
                        .requestMatchers("/invoices/**").hasAnyRole("ADMIN", "FRONT_OFFICE")

                        // 4. Buchungsprozess: Nur eingeloggte User
                        .requestMatchers("/bookings/create/**").authenticated()

                        // 5. Alles andere sperren
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true) // Nach Login zur Homepage
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // Nach Logout zur Homepage
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable()); // CSRF deaktivieren für einfache Entwicklung

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}