package com.auctionsystem.auctionhouse.service;

import com.auctionsystem.auctionhouse.dto.UserDto;
import com.auctionsystem.auctionhouse.entity.User;
import com.auctionsystem.auctionhouse.mapper.UserMapper;
import com.auctionsystem.auctionhouse.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public UserDto saveUser(UserDto userDto) {
        if (userDto.getUsername() == null || userDto.getPassword() == null) {
            throw new IllegalArgumentException("Nazwa użytkownika i hasło są wymagane");
        }
        Optional<User> existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Użytkownik o takiej nazwie już istnieje");
        }
        User user = userMapper.toEntity(userDto);
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
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
    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto);
    }

    @Transactional
    public Optional<User> getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public Optional<User> getUserEntityById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUser(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o podanym ID"));

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
        return userMapper.toDto(updatedUser);
    }



    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean isUserAuthorizedToUpdate(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Optional<UserDto> existingUserDto = getUserById(id);
        return existingUserDto.isPresent() && existingUserDto.get().getUsername().equals(currentPrincipalName);
    }
}
