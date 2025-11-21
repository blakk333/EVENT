package com.eventaro.Eventaro.config;

import com.eventaro.Eventaro.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. Statische Ressourcen (CSS, Bilder) IMMER erlauben
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.ico").permitAll()

                        // 2. Öffentliche Seiten (Login, Fehler, Events ansehen)
                        .requestMatchers("/login", "/register", "/error").permitAll()
                        .requestMatchers("/events/view", "/events/details/**").permitAll()

                        // 3. Buchungsprozess (Gäste müssen zugreifen können)
                        .requestMatchers("/bookings/**").permitAll()

                        // 4. Geschützte Bereiche (Nur für Admins / Backoffice)
                        .requestMatchers("/events/create", "/events/edit/**", "/events/delete/**").hasRole("ADMIN")
                        .requestMatchers("/dashboard", "/log").hasRole("ADMIN")
                        .requestMatchers("/invoices/**").hasRole("ADMIN") // Rechnungen nur für Admins

                        // 5. Alles andere sperren
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Unsere eigene HTML Seite
                        .defaultSuccessUrl("/dashboard", true) // Nach Login immer zum Dashboard
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // Nach Logout zurück zum Login
                        .permitAll()
                )
                // CSRF ignorieren wir für dieses Projekt temporär, um Probleme mit POST-Buttons zu vermeiden
                // (In Produktion würde man das aktivieren und Tokens im HTML nutzen)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }
}