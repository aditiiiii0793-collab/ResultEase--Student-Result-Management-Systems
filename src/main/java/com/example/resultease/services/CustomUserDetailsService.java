package com.example.resultease.services; // Ensure this matches your package

import com.example.resultease.models.User;
import com.example.resultease.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Debugging output
        System.out.println("🔍 Loading user: " + username);
        System.out.println("   Role from DB: " + user.getRole());
        System.out.println("   Password hash from DB: " + user.getPassword());

        // Use User.builder() for clarity and remove ROLE_ prefix for .roles()
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Pass the encoded password from DB
                .roles(user.getRole().replace("ROLE_", "")) // Use .roles() which expects names without ROLE_
                .build();
    }
}