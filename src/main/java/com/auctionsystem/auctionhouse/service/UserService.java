package com.auctionsystem.auctionhouse.service;

import com.auctionsystem.auctionhouse.dto.UserDto;
import com.auctionsystem.auctionhouse.entity.User;
import com.auctionsystem.auctionhouse.mapper.UserMapper;
import com.auctionsystem.auctionhouse.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDto saveUser(User user) {
        if (user.getUsername() == null || user.getPasswordHash() == null) {
            throw new IllegalArgumentException("Nazwa użytkownika i hasło są wymagane");
        }
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Użytkownik o takiej nazwie już istnieje");
        }
        String encodedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(encodedPassword);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }

    @Transactional
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUser(User user) {
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
