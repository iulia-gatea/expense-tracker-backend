package org.example.controller;

import org.example.dto.AppUserDTO;
import org.example.dto.AuthDTO;
import org.example.dto.AuthResponseDTO;
import org.example.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private AppUserDTO testUserDTO;
    private AuthDTO testAuthDTO;
    private AuthResponseDTO successResponse;
    private AuthResponseDTO errorResponse;

    @BeforeEach
    public void setUp() {
        testUserDTO = new AppUserDTO();
        testUserDTO.setFullName("Test User");
        testUserDTO.setUsername("testuser");
        testUserDTO.setPassword("password123");

        testAuthDTO = new AuthDTO();
        testAuthDTO.setUsername("testuser");
        testAuthDTO.setPassword("password123");

        successResponse = new AuthResponseDTO("jwt-token", "success");
        errorResponse = new AuthResponseDTO(null, "Error: invalid username or password");
    }

    @Test
    public void testSignup_WithNewUser_ShouldReturnOkResponse() {
        // Arrange
        when(authService.registerUser(testUserDTO)).thenReturn(successResponse);

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.signup(testUserDTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo("jwt-token");
        assertThat(response.getBody().getMessage()).isEqualTo("success");
        verify(authService, times(1)).registerUser(testUserDTO);
    }

    @Test
    public void testSignup_WithExistingUsername_ShouldReturnBadRequest() {
        // Arrange
        AuthResponseDTO errorResp = new AuthResponseDTO(null, "error: Username is already taken");
        when(authService.registerUser(testUserDTO)).thenReturn(errorResp);

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.signup(testUserDTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNull();
        assertThat(response.getBody().getMessage()).contains("error");
        verify(authService, times(1)).registerUser(testUserDTO);
    }

    @Test
    public void testLogin_WithValidCredentials_ShouldReturnOkResponse() {
        // Arrange
        when(authService.loginUser(testAuthDTO)).thenReturn(successResponse);

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.login(testAuthDTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo("jwt-token");
        assertThat(response.getBody().getMessage()).isEqualTo("success");
        verify(authService, times(1)).loginUser(testAuthDTO);
    }

    @Test
    public void testLogin_WithInvalidCredentials_ShouldReturnBadRequest() {
        // Arrange
        when(authService.loginUser(testAuthDTO)).thenReturn(errorResponse);

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.login(testAuthDTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNull();
        assertThat(response.getBody().getMessage()).contains("Error");
        verify(authService, times(1)).loginUser(testAuthDTO);
    }

    @Test
    public void testSignup_ShouldReturnTokenInSuccessResponse() {
        // Arrange
        when(authService.registerUser(testUserDTO)).thenReturn(successResponse);

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.signup(testUserDTO);

        // Assert
        assertThat(response.getBody().getToken()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo("jwt-token");
    }

    @Test
    public void testLogin_ShouldPassCorrectAuthDTOToService() {
        // Arrange
        when(authService.loginUser(any(AuthDTO.class))).thenReturn(successResponse);

        // Act
        authController.login(testAuthDTO);

        // Assert
        verify(authService, times(1)).loginUser(argThat(auth ->
                auth.getUsername().equals("testuser") &&
                auth.getPassword().equals("password123")
        ));
    }

    @Test
    public void testSignup_ShouldPassCorrectUserDTOToService() {
        // Arrange
        when(authService.registerUser(any(AppUserDTO.class))).thenReturn(successResponse);

        // Act
        authController.signup(testUserDTO);

        // Assert
        verify(authService, times(1)).registerUser(argThat(user ->
                user.getUsername().equals("testuser") &&
                user.getFullName().equals("Test User") &&
                user.getPassword().equals("password123")
        ));
    }

    @Test
    public void testLogin_WithSuccessMessage_ShouldReturnOk() {
        // Arrange
        AuthResponseDTO response = new AuthResponseDTO("token", "success");
        when(authService.loginUser(testAuthDTO)).thenReturn(response);

        // Act
        ResponseEntity<AuthResponseDTO> result = authController.login(testAuthDTO);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testLogin_WithErrorMessage_ShouldReturnBadRequest() {
        // Arrange
        AuthResponseDTO response = new AuthResponseDTO(null, "Error: some error message");
        when(authService.loginUser(testAuthDTO)).thenReturn(response);

        // Act
        ResponseEntity<AuthResponseDTO> result = authController.login(testAuthDTO);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testSignup_WithSuccessMessage_ShouldReturnOk() {
        // Arrange
        AuthResponseDTO response = new AuthResponseDTO("token", "success");
        when(authService.registerUser(testUserDTO)).thenReturn(response);

        // Act
        ResponseEntity<AuthResponseDTO> result = authController.signup(testUserDTO);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testSignup_WithErrorMessage_ShouldReturnBadRequest() {
        // Arrange
        AuthResponseDTO response = new AuthResponseDTO(null, "error: Username is already taken");
        when(authService.registerUser(testUserDTO)).thenReturn(response);

        // Act
        ResponseEntity<AuthResponseDTO> result = authController.signup(testUserDTO);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}





