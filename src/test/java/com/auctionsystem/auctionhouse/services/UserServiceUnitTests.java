package com.auctionsystem.auctionhouse.services;

import com.auctionsystem.auctionhouse.dtos.UserDto;
import com.auctionsystem.auctionhouse.entities.User;
import com.auctionsystem.auctionhouse.mappers.UserMapper;
import com.auctionsystem.auctionhouse.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class UserServiceUnitTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveUser_Success() {
        // Given
        UserDto userDto = createUserDto(1L);

        User user = createUser(1L);

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        UserDto result = userService.saveUser(userDto);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername(userDto.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testSaveUser_noPasswordOrUsername() {
        UserDto userDto = createUserDto(1L);
        userDto.setUsername(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser(userDto);
        });

        assertEquals("Username and password are required", exception.getMessage());

        userDto.setUsername("testuser");
        userDto.setPassword(null);

        exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser(userDto);
        });

        assertEquals("Username and password are required", exception.getMessage());
    }

    @Test
    public void testSaveUser_userAlreadyExists() {
        UserDto userDto = createUserDto(1L);

        User existingUser = createUser(2L);

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(existingUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser(userDto);
        });

        assertEquals("User with such username already exists", exception.getMessage());
    }

    @Test
    public void testGetUserByID() {
        // Given
        User user = createUser(1L);

        UserDto userDto = createUserDto(1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        Optional<UserDto> result = userService.getUserById(user.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository, times(1)).findById(user.getId());
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    public void testGetAllUsers() {
        // Given
        User user1 = createUser(1L);
        User user2 = createUser(2L);
        User user3 = createUser(3L);
        List<User> userList = Arrays.asList(user1, user2, user3);


        UserDto userDto1 = createUserDto(1L);
        UserDto userDto2 = createUserDto(2L);
        UserDto userDto3 = createUserDto(3L);
        List<UserDto> userDtoList = Arrays.asList(userDto1, userDto2, userDto3);


        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toDto(user1)).thenReturn(userDto1);
        when(userMapper.toDto(user2)).thenReturn(userDto2);
        when(userMapper.toDto(user3)).thenReturn(userDto3);

        // When
        List<UserDto> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.containsAll(userDtoList));
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDto(user1);
        verify(userMapper, times(1)).toDto(user2);
        verify(userMapper, times(1)).toDto(user3);
    }

    @Test
    public void testUpdateUser() {
        // Given
        UserDto userDto = createUserDto(1L);

        User user = createUser(1L);

        User updatedUser = createUser(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updateduser@example.com");
        updatedUser.setPasswordHash(passwordEncoder.encode("updatedpassword"));

        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(userDto);

        // When
        UserDto result = userService.updateUser(userDto);

        // Then
        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getUsername(), result.getUsername());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(userDto.getId());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toDto(updatedUser);
    }

    @Test
    public void testDeleteUser() {
        // Given
        User user = createUser(1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // When
        userService.deleteUser(user.getId());

        // Then
        verify(userRepository, times(1)).deleteById(user.getId());
    }

    public UserDto createUserDto(Long id) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setUsername("testuser");
        userDto.setPassword("password");
        userDto.setEmail("testuser@mail.com");
        return userDto;
    }

    public User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("testuser");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setEmail("testuser@mail.com");
        return user;
    }
}
