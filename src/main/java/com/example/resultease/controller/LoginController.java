package com.example.resultease.controller;

import com.example.resultease.models.User;
import com.example.resultease.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;
import java.util.Set;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        // Check if user must change password
        if (userOpt.isPresent() && userOpt.get().getMustChangePassword()) {
            return "redirect:/change-password"; // Force redirect
        }

        // Otherwise, redirect based on role
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains("ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        if (roles.contains("ROLE_TEACHER")) {
            return "redirect:/teacher/dashboard";
        }
        if (roles.contains("ROLE_STUDENT")) {
            return "redirect:/student/dashboard";
        }
        return "redirect:/login?error"; // Fallback
    }
}