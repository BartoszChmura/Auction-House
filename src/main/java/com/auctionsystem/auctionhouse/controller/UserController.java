package com.auctionsystem.auctionhouse.controller;

import com.auctionsystem.auctionhouse.dto.UserDto;
import com.auctionsystem.auctionhouse.mapper.UserMapper;
import com.auctionsystem.auctionhouse.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.auctionsystem.auctionhouse.entity.User;
import com.auctionsystem.auctionhouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }
    @RequestMapping("/register")
    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {
            User user = userMapper.toEntity(userDto);
            UserDto savedUser = userService.saveUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }



}
