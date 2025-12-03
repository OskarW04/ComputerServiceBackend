package com.example.ComputerService.controller;

import com.example.ComputerService.repository.UserRepository;
import com.example.ComputerService.model.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Pamiętaj o importach swoich klas!
// import com.twojapaczka.model.User;
// import com.twojapaczka.repository.UserRepository;

@RestController
@RequestMapping("/api") // Opcjonalny prefiks
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/add-user")
    public ResponseEntity<?> addUser(@RequestBody Client user) {
        try {
            Client savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Błąd zapisu: " + e.getMessage());
        }
    }
}