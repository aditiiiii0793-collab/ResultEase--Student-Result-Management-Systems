package com.example.resultease.controller;

import com.example.resultease.models.User;
import com.example.resultease.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserSettingsController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "change_password";
    }

    @PostMapping("/change-password")
    public String processChangePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);

        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Error: Could not retrieve current user data.");
            return "redirect:/change-password";
        }
        if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Incorrect current password.");
            return "redirect:/change-password";
        }

        // --- CORRECTED: Check length AND if it's just spaces ---
        if (newPassword.trim().length() < 8) {
            redirectAttributes.addFlashAttribute("error", "New password must be at least 8 characters long and cannot be just spaces.");
            return "redirect:/change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password and confirmation password do not match.");
            return "redirect:/change-password";
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        currentUser.setMustChangePassword(false);
        userRepository.save(currentUser);

        redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        return "redirect:/default";
    }
}