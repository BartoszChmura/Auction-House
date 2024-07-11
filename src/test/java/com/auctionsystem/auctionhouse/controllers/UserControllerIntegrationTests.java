package com.auctionsystem.auctionhouse.controllers;

import com.auctionsystem.auctionhouse.dtos.UserDto;
import com.auctionsystem.auctionhouse.entities.User;
import com.auctionsystem.auctionhouse.repositories.UserRepository;
import com.auctionsystem.auctionhouse.services.JwtService;
import com.auctionsystem.auctionhouse.services.JwtUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
public class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    public void setup() {
        userRepository.save(createUser(1L, "testuser1"));
        userRepository.save(createUser(2L, "testuser2"));
        userRepository.save(createUser(3L, "testuser3"));

        // Granting authentication to testuser1
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername("testuser1");
        jwtToken = jwtService.generateToken(userDetails);
    }

    @Test
    public void testRegisterUser_OK() throws Exception {
        UserDto userDto = createUserDto(4L, "testuser4");

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.username").value("testuser4"));
    }

    @Test
    public void testRegisterUser_Conflict() throws Exception {
        UserDto userDto = createUserDto(10L, "testuser10");

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isConflict());
    }

    @Test
    public void getUserById_OK() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/user/" + userId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser1"))
                .andExpect(jsonPath("$.email").value("testuser@mail.com"));
    }

    @Test
    public void getUserById_NotFound() throws Exception {
        Long userId = 15L;

        mockMvc.perform(get("/user/" + userId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllUsers_OK() throws Exception {
        mockMvc.perform(get("/user/all")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUser_OK() throws Exception {
        UserDto userDto = createUserDto(1L, "testuser1");
        userDto.setEmail("test@mail.com");


        mockMvc.perform(MockMvcRequestBuilders.put("/user/{id}", 1L)

                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser1"))
                .andExpect(jsonPath("$.email").value("test@mail.com"));
    }

    @Test
    public void updateUser_NotFound() throws Exception {
        UserDto userDto = createUserDto(2L, "testuser2");
        userDto.setEmail("test@mail.com");

        mockMvc.perform(MockMvcRequestBuilders.put("/user/{id}", 15L)

                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_Unauthorized() throws Exception {
        UserDto userDto = createUserDto(2L, "testuser2");
        userDto.setEmail("test@mail.com");


        mockMvc.perform(MockMvcRequestBuilders.put("/user/{id}", 2L)

                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteUser_OK() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/user/" + userId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUser_NotAuthenticated() throws Exception {
        Long userId = 2L;

        mockMvc.perform(delete("/user/" + userId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401));
    }

    @Test
    public void deleteUser_NotFound() throws Exception {
        Long userId = 15L;

        mockMvc.perform(delete("/user/" + userId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    public UserDto createUserDto(Long id, String username) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setUsername(username);
        userDto.setPassword("password");
        userDto.setEmail("testuser@mail.com");
        return userDto;
    }

    public User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setEmail("testuser@mail.com");
        return user;
    }
}
