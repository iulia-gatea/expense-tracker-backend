package org.example.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    public void testGenerateToken_ShouldCreateValidToken() {
        // Arrange
        String username = "testuser";

        // Act
        String token = jwtUtil.generateToken(username);

        // Assert
        assertThat(token).isNotNull().isNotEmpty();
        assertThat(token).contains(".");  // JWT format has dots
    }

    @Test
    public void testExtractUsername_ShouldReturnCorrectUsername() {
        // Arrange
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    public void testValidateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // Act
        boolean isValid = jwtUtil.validateToken(token, username);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    public void testValidateToken_WithInvalidUsername_ShouldReturnFalse() {
        // Arrange
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // Act
        boolean isValid = jwtUtil.validateToken(token, "differentuser");

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    public void testValidateToken_WithNullToken_ShouldThrowException() {
        // Assert
        assertThatThrownBy(() -> jwtUtil.validateToken(null, "testuser"))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void testGenerateToken_DifferentUsers_ShouldGenerateDifferentTokens() {
        // Arrange
        String user1 = "user1";
        String user2 = "user2";

        // Act
        String token1 = jwtUtil.generateToken(user1);
        String token2 = jwtUtil.generateToken(user2);

        // Assert
        assertThat(token1).isNotEqualTo(token2);
    }
}

