package com.auctionsystem.auctionhouse.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.auctionsystem.auctionhouse.entity.User;
import com.auctionsystem.auctionhouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {



}
