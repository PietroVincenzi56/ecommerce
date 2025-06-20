package com.example.ecommerce.controllers;

import com.example.ecommerce.entities.User;
import com.example.ecommerce.exceptions.MailUserAlreadyExistsException;
import com.example.ecommerce.other.Message;
import com.example.ecommerce.other.Utils;
import com.example.ecommerce.repositories.UserRepository;
import com.example.ecommerce.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity registerUser(@RequestBody User user) {
        try{
            userService.registerUser(user);
        } catch (MailUserAlreadyExistsException e) {
            return new ResponseEntity<>(new Message("email esistente"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new Message("utente registrato"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    // Recupera dati dell’utente attualmente loggato (con sincronizzazione da Keycloak)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<?> getLoggedUserInfo() {
        User currentUser = userService.syncUserWithKeycloak();
        return ResponseEntity.ok(currentUser);
    }
}




