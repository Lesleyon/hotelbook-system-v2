package org.example.controllers;

import org.example.entities.User;
import org.example.repositories.UserRepository;
import org.example.jwt.JWTservice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class AuthController {
    private final UserRepository userRep;
    private final PasswordEncoder encoder;
    private final JWTservice jwtService;

    public AuthController(UserRepository userRep, PasswordEncoder encoder, JWTservice jwtService) {
        this.userRep = userRep;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User createUser(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("USER");
        return userRep.save(user);
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("USER");
        userRep.save(user);
        return Map.of("token", jwtService.generateToken(user.getUsername(), user.getRole()));
    }

    @PostMapping("/auth")
    public Map<String, String> auth(@RequestBody User user) {
        User existing = userRep.findByUsername(user.getUsername());
        if (existing == null) throw new RuntimeException("User not found");
        if (!encoder.matches(user.getPassword(), existing.getPassword())) {
            throw new RuntimeException("Bad credentials");
        }
        return Map.of("token", jwtService.generateToken(existing.getUsername(), existing.getRole()));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@RequestParam String username) {
        userRep.deleteByUsername(username);
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(@RequestBody User user) {
        User existing = userRep.findByUsername(user.getUsername());
        if (existing == null) throw new RuntimeException("User not found");
        if (user.getPassword() != null) existing.setPassword(encoder.encode(user.getPassword()));
        if (user.getRole() != null) existing.setRole(user.getRole());
        return userRep.save(existing);
    }
}