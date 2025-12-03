package com.example.ComputerService.controller;

import com.example.ComputerService.repository.ClientRepository;
import com.example.ComputerService.model.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // Opcjonalny prefiks
public class UserController {

    private final ClientRepository clientRepository;

    public UserController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @PostMapping("/add-user")
    public ResponseEntity<?> addUser(@RequestBody Client user) {
        try {
            Client savedUser = clientRepository.save(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Błąd zapisu: " + e.getMessage());
        }
    }
}