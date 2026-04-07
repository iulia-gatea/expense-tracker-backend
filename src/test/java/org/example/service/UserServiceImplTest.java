package org.example.service;

import org.example.model.AppUser;
import org.example.model.Role;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private AppUser testUser;

    @BeforeEach
    public void setUp() {
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.USER);
    }

    @Test
    public void testSaveUser_ShouldSaveAndReturnUser() {
        // Arrange
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        AppUser savedUser = userService.saveUser(testUser);

        // Assert
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getFullName()).isEqualTo("Test User");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    public void testFindByUsename_WithExistingUser_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        AppUser foundUser = userService.findByUsename("testuser");

        // Assert
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    public void testFindByUsename_WithNonExistingUser_ShouldReturnNull() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        AppUser foundUser = userService.findByUsename("nonexistent");

        // Assert
        assertThat(foundUser).isNull();
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    public void testFindUserById_ShouldReturnEmptyOptional() {
        // Act
        Optional<AppUser> result = userService.findUserById(1L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    public void testSaveUser_WithMultipleUsers_ShouldSaveAll() {
        // Arrange
        AppUser user2 = new AppUser();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setFullName("User Two");
        user2.setRole(Role.USER);

        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userRepository.save(user2)).thenReturn(user2);

        // Act
        AppUser saved1 = userService.saveUser(testUser);
        AppUser saved2 = userService.saveUser(user2);

        // Assert
        assertThat(saved1.getUsername()).isEqualTo("testuser");
        assertThat(saved2.getUsername()).isEqualTo("user2");
        verify(userRepository, times(2)).save(any(AppUser.class));
    }
}

