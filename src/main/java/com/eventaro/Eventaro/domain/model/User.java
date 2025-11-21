package com.eventaro.Eventaro.domain.model;

import com.eventaro.Eventaro.enums.UserRole;
import jakarta.persistence.*;

@Entity
@Table(name = "app_users") // "user" ist in Postgres ein reserviertes Wort, daher "app_users"
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username; // Login Name

    @Column(nullable = false)
    private String password; // Verschlüsseltes Passwort

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // Verknüpfung zu einem Customer, falls es ein Kunde ist (optional für später)
    // private Integer customerId;

    public User() {}

    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter & Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}