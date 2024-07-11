package com.auctionsystem.auctionhouse.controllers;

import com.auctionsystem.auctionhouse.dtos.CategoryDto;
import com.auctionsystem.auctionhouse.entities.Category;
import com.auctionsystem.auctionhouse.entities.User;
import com.auctionsystem.auctionhouse.repositories.CategoryRepository;
import com.auctionsystem.auctionhouse.repositories.UserRepository;
import com.auctionsystem.auctionhouse.services.JwtService;
import com.auctionsystem.auctionhouse.services.JwtUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
public class CategoryControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    private String jwtToken;

    @BeforeEach
    public void setup() {
        userRepository.save(createUser(1L, "testuser1"));

        categoryRepository.save(createCategory(1L, "Elektronika"));

        // Granting authentication to testuser1
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername("testuser1");
        jwtToken = jwtService.generateToken(userDetails);
    }

    @Test
    public void testAddCategory_OK() throws Exception {
        CategoryDto categoryDto = createCategoryDto(2L, "Książki");

        mockMvc.perform(post("/category/add")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.categoryName").value("Książki"));
    }

    @Test
    public void testAddCategory_Conflict() throws Exception {
        CategoryDto categoryDto = createCategoryDto(1L, "Elektronika");

        mockMvc.perform(post("/category/add")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetCategoryById_OK() throws Exception {
        mockMvc.perform(get("/category/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.categoryName").value("Elektronika"));
    }

    @Test
    public void testGetCategoryById_NotFound() throws Exception {
        mockMvc.perform(get("/category/10")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Przedmiot o id 10 nie istnieje"));
    }

    @Test
    public void testGetAllCategories_OK() throws Exception {
        mockMvc.perform(get("/category/all")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].categoryName").value("Elektronika"));
    }

    @Test
    public void testUpdateCategory_OK() throws Exception {
        CategoryDto categoryDto = createCategoryDto(1L, "Elektronika");

        categoryDto.setCategoryName("Książki");

        mockMvc.perform(put("/category/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.categoryName").value("Książki"));
    }

    @Test
    public void testUpdateCategory_NotFound() throws Exception {
        CategoryDto categoryDto = createCategoryDto(10L, "Elektronika");

        mockMvc.perform(put("/category/10")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCategory_OK() throws Exception {
        mockMvc.perform(delete("/category/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteCategory_NotFound() throws Exception {
        mockMvc.perform(delete("/category/10")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    public Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setCategoryName(name);
        return category;
    }

    public CategoryDto createCategoryDto(Long id, String name) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(id);
        categoryDto.setCategoryName(name);
        return categoryDto;
    }

    public User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPasswordHash("password");
        user.setEmail("testuser@mail.com");
        return user;
    }
}
