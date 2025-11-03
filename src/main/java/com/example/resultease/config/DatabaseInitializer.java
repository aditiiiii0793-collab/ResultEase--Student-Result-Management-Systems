package com.example.resultease.config; // Ensure this matches your package

import com.example.resultease.models.User;
import com.example.resultease.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // --- THIS IS THE FIX ---
        // Only add default users if the user table is completely empty
        if (userRepository.count() == 0) {
            System.out.println("No users found, initializing default users...");

            // --- Create admin user ---
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            admin.setEmail("admin@example.com");
            admin.setMustChangePassword(false); // Admin doesn't need to change
            userRepository.save(admin);
            System.out.println("✅ Created admin user: admin / admin123");

            // --- Create a generic TEST teacher ---
            User teacher = new User();
            teacher.setUsername("teacher@example.com");
            teacher.setPassword(passwordEncoder.encode("teacher123"));
            teacher.setRole("ROLE_TEACHER");
            teacher.setEmail("teacher@example.com");
            teacher.setMustChangePassword(true); // Teacher must change
            userRepository.save(teacher);
            System.out.println("✅ Created teacher user: teacher@example.com / teacher123");

            // --- Create a generic TEST student ---
            User student = new User();
            student.setUsername("student@example.com");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setRole("ROLE_STUDENT");
            student.setEmail("student@example.com");
            student.setMustChangePassword(true);
            userRepository.save(student);
            System.out.println("✅ Created student user: student@example.com / student123");
        } else {
            System.out.println("Database already contains users. Skipping initialization.");
        }
    }
}