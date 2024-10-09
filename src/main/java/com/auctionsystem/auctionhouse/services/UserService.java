package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.dtos.UserDto;
import com.auctionsystem.auctionhouse.entities.User;
import com.auctionsystem.auctionhouse.mappers.UserMapper;
import com.auctionsystem.auctionhouse.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    public UserDto saveUser(UserDto userDto) {
        log.info("Saving user with id {}", userDto.getId());
        if (userDto.getUsername() == null || userDto.getPassword() == null) {
            throw new IllegalArgumentException("Username and password are required");
        }
        Optional<User> existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with such username already exists");
        }
        User user = userMapper.toEntity(userDto);
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPasswordHash(encodedPassword);
        User savedUser = userRepository.save(user);
        log.info("Saved user with id: {}", savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    @Transactional
    public Optional<UserDto> getUserById(Long id) {
        log.info("Retrieving user with id: {}", id);
        Optional<UserDto> result = userRepository.findById(id)
                .map(userMapper::toDto);
        log.info("Retrieved user with id: {}", id);

        return result;
    }

    @Transactional
    public Optional<UserDto> getUserByUsername(String username) {
        log.info("Retrieving user with username: {}", username);
        Optional<UserDto> result = userRepository.findByUsername(username)
                .map(userMapper::toDto);
        log.info("Retrieved user with username: {}", username);

        return result;
    }

    @Transactional
    public Optional<User> getUserEntityByUsername(String username) {
        log.info("Retrieving user entity with username: {}", username);
        Optional<User> result = userRepository.findByUsername(username);
        log.info("Retrieved user entity with username: {}", username);

        return result;
    }

    @Transactional
    public Optional<User> getUserEntityById(Long id) {
        log.info("Retrieving user entity with id: {}", id);
        Optional<User> result = userRepository.findById(id);
        log.info("Retrieved user entity with id: {}", id);

        return result;
    }

    @Transactional
    public List<UserDto> getAllUsers() {
        log.info("Retrieving all users");
        List<User> users = userRepository.findAll();
        List<UserDto> result = users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        log.info("Retrieved all users");

        return result;
    }

    @Transactional
    public UserDto updateUser(UserDto userDto) {
        log.info("Updating user with id: {}", userDto.getId());
        User existingUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("User with such ID not found"));

        if (userDto.getUsername() != null && !userDto.getUsername().isEmpty()) {
            existingUser.setUsername(userDto.getUsername());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with id: {}", userDto.getId());

        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
        log.info("User with id {} has been successfully deleted", id);
    }

    public boolean isUserAuthorizedToUpdate(Long id) {
        log.info("Checking if user is authorized to update user with id: {}", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        log.info("Current user name: {}", currentPrincipalName);

        Optional<UserDto> existingUserDto = getUserById(id);
        boolean isAuthorized = existingUserDto.isPresent() && existingUserDto.get().getUsername().equals(currentPrincipalName);
        log.info("User is {} to update user with id: {}", isAuthorized ? "authorized" : "not authorized", id);

        return isAuthorized;
    }
}