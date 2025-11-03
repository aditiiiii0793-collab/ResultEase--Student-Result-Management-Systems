package com.example.resultease.services;

import com.example.resultease.models.User;
import com.example.resultease.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID; // For generating tokens

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Method to create and save a reset token for a user found by email
    public Optional<User> createPasswordResetTokenForUser(String email) {
        // Find user by email (Need to add findByEmail method to UserRepository)
        Optional<User> userOptional = userRepository.findByEmail(email); // Assuming User model has email

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString(); // Generate a random token
            user.setResetPasswordToken(token);
            // Set expiry time (e.g., 15 minutes from now)
            user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user); // Save token and expiry to the user record
            // In a real app, you would email the token link here
             System.out.println("Password Reset Token for " + email + ": " + token); // Print token for testing
            return Optional.of(user);
        }
        return Optional.empty(); // User not found
    }

    // Method to validate a token
    public Optional<User> validatePasswordResetToken(String token) {
        Optional<User> userOptional = userRepository.findByResetPasswordToken(token); // Need this method in repo

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Check if token has expired
            if (user.getResetPasswordTokenExpiry().isAfter(LocalDateTime.now())) {
                return Optional.of(user); // Token is valid
            } else {
                // Token expired, clear it (optional)
                user.setResetPasswordToken(null);
                user.setResetPasswordTokenExpiry(null);
                userRepository.save(user);
            }
        }
        return Optional.empty(); // Token not found or expired
    }

    // Method to change the password using a token
    public void changeUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword)); // Encode new password
        user.setResetPasswordToken(null); // Clear the token
        user.setResetPasswordTokenExpiry(null); // Clear expiry
        userRepository.save(user); // Save the updated user
    }
}