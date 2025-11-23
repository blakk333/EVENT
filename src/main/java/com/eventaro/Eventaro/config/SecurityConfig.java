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
                        // Statische Ressourcen
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.ico").permitAll()

                        // Öffentliche Seiten (Home, Details, Login, Register)
                        .requestMatchers("/", "/login", "/register", "/error").permitAll()
                        .requestMatchers("/events/details/**", "/events/details/*/image").permitAll()

                        // Backoffice nur für Admin/Mitarbeiter
                        .requestMatchers("/dashboard", "/log", "/categories/**", "/organizers/**").hasAnyRole("ADMIN", "FRONT_OFFICE")
                        .requestMatchers("/events/create", "/events/edit/**").hasRole("ADMIN")

                        // Buchungsprozess (Login erforderlich für finales Buchen, Formular anzeigen geht evtl. auch so, aber wir sichern ab)
                        .requestMatchers("/bookings/create/**").authenticated() // Nur eingeloggte User dürfen buchen

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