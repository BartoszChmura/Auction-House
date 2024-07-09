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
        log.info("Zapisywanie użytkownika o id {}", userDto.getId());
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
        log.info("Zapisano użytkownika o id: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public Optional<UserDto> getUserById(Long id) {
        log.info("Pobieranie użytkownika o id: {}", id);
        Optional<UserDto> result = userRepository.findById(id)
                .map(userMapper::toDto);
        log.info("Pobrano użytkownika o id: {}", id);
        return result;
    }

    @Transactional
    public Optional<UserDto> getUserByUsername(String username) {
        log.info("Pobieranie użytkownika o nazwie: {}", username);
        Optional<UserDto> result = userRepository.findByUsername(username)
                .map(userMapper::toDto);
        log.info("Pobrano użytkownika o nazwie: {}", username);
        return result;
    }

    @Transactional
    public Optional<User> getUserEntityByUsername(String username) {
        log.info("Pobieranie encji użytkownika o nazwie: {}", username);
        Optional<User> result = userRepository.findByUsername(username);
        log.info("Pobrano encję użytkownika o nazwie: {}", username);
        return result;
    }

    @Transactional
    public Optional<User> getUserEntityById(Long id) {
        log.info("Pobieranie encji użytkownika o id: {}", id);
        Optional<User> result = userRepository.findById(id);
        log.info("Pobrano encję użytkownika o id: {}", id);
        return result;
    }

    @Transactional
    public List<UserDto> getAllUsers() {
        log.info("Pobieranie wszystkich użytkowników");
        List<User> users = userRepository.findAll();
        List<UserDto> result = users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        log.info("Pobrano wszystkich użytkowników");
        return result;
    }

    @Transactional
    public UserDto updateUser(UserDto userDto) {
        log.info("Aktualizacja użytkownika o id: {}", userDto.getId());
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
        log.info("Użytkownik zaktualizowany pomyślnie o id: {}", userDto.getId());
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Usuwanie użytkownika o id: {}", id);
        userRepository.deleteById(id);
        log.info("Użytkownik o id {} został pomyślnie usunięty", id);
    }

    public boolean isUserAuthorizedToUpdate(Long id) {
        log.info("Sprawdzanie, czy użytkownik jest upoważniony do aktualizacji użytkownika o id: {}", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        log.info("Nazwa aktualnego użytkownika: {}", currentPrincipalName);

        Optional<UserDto> existingUserDto = getUserById(id);
        boolean isAuthorized = existingUserDto.isPresent() && existingUserDto.get().getUsername().equals(currentPrincipalName);
        log.info("Użytkownik jest {} do aktualizacji użytkownika o id: {}", isAuthorized ? "upoważniony" : "nieupoważniony", id);
        return isAuthorized;
    }
}
