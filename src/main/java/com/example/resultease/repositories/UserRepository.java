package com.example.resultease.repositories;
import com.example.resultease.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Method to find a user by their username
    Optional<User> findByUsername(String username);
    // Add these methods
    Optional<User> findByEmail(String email); // Assuming User model has email
    Optional<User> findByResetPasswordToken(String token);
}