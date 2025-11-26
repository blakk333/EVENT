package com.eventaro.Eventaro.service;

import com.eventaro.Eventaro.model.User;
import com.eventaro.Eventaro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword()) // Muss bereits encoded sein!
                .roles(user.getRole())
                .build();
    }

    // Hilfsmethode zum Anlegen neuer Benutzer (Passwort verschlüsseln!)
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    // Liste aller Benutzer
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Benutzer löschen
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}