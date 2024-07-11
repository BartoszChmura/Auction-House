package com.auctionsystem.auctionhouse.controllers;

import com.auctionsystem.auctionhouse.dtos.UserDto;
import com.auctionsystem.auctionhouse.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;



    @Autowired

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {
            UserDto savedUser = userService.saveUser(userDto);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<UserDto> userDto = userService.getUserById(id);
        if (userDto.isPresent()) {
            return ResponseEntity.ok(userDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Użytkownik o id " + id + " nie istnieje");
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<UserDto> userDto = userService.getUserByUsername(username);
        if (userDto.isPresent()) {
            return ResponseEntity.ok(userDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Użytkownik o nazwie " + username + " nie istnieje");
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        Optional<UserDto> existingUser = userService.getUserById(id);
        if (existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Użytkownik o id " + id + " nie istnieje");
        }
        if (!userService.isUserAuthorizedToUpdate(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nie masz uprawnień do aktualizacji danych innego użytkownika");
        }

        userDto.setId(id);
        UserDto updatedUserDto = userService.updateUser(userDto);
        return ResponseEntity.ok(updatedUserDto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<UserDto> existingUser = userService.getUserById(id);
        if (existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Użytkownik o id " + id + " nie istnieje");
        }
        if (!userService.isUserAuthorizedToUpdate(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nie masz uprawnień do usunięcia innego użytkownika");
        }

        userService.deleteUser(id);
        return ResponseEntity.ok("Użytkownik o id " + id + " został usunięty");
    }
}
