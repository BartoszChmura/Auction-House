package com.auctionsystem.auctionhouse.controller;

import com.auctionsystem.auctionhouse.dto.UserDto;
import com.auctionsystem.auctionhouse.mapper.UserMapper;
import com.auctionsystem.auctionhouse.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.auctionsystem.auctionhouse.entity.User;
import com.auctionsystem.auctionhouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
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
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        if (!userService.isUserAuthorizedToUpdate(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nie masz uprawnień do aktualizacji danych innego użytkownika");
        }

        userDto.setId(id);
        UserDto updatedUserDto = userService.updateUser(userDto);
        return ResponseEntity.ok(updatedUserDto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userService.isUserAuthorizedToUpdate(id)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nie masz uprawnień do usunięcia innego użytkownika");
        }

        userService.deleteUser(id);
        return ResponseEntity.ok("Użytkownik o id " + id + " został usunięty");
    }
}
