package com.eventaro.Eventaro.config;

import com.eventaro.Eventaro.application.user.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/webjars/**").permitAll()
                        // Public Pages inkl. Register/Login erlauben
                        .requestMatchers("/", "/home", "/public/**", "/login", "/register", "/error").permitAll()

                        // Kundenbereich
                        .requestMatchers("/my-profile/**").authenticated()

                        // Backoffice/Admin Bereiche
                        .requestMatchers("/dashboard/**").hasAnyRole("FRONTOFFICE", "BACKOFFICE", "ADMIN")
                        .requestMatchers("/events/**", "/bookings/**", "/invoices/**", "/categories/**").hasAnyRole("FRONTOFFICE", "BACKOFFICE", "ADMIN")
                        .requestMatchers("/admin/**", "/log/**", "/users/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login") // Eigene Login Seite
                        .defaultSuccessUrl("/redirect-by-role", true) // Logik wohin nach Login
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutSuccessUrl("/home")
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}