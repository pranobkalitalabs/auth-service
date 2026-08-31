package com.platform.auth.util;

import com.platform.auth.domain.entity.Address;
import com.platform.auth.domain.entity.Role;
import com.platform.auth.domain.entity.User;
import com.platform.auth.domain.enums.AuthProvider;
import com.platform.auth.domain.enums.RoleType;
import com.platform.auth.repository.RoleRepository;
import com.platform.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default-email:admin@platform.com}")
    private String defaultAdminEmail;

    @Value("${app.admin.default-password:Admin@123456}")
    private String defaultAdminPassword;

    @Value("${app.admin.default-first-name:System}")
    private String defaultFirstName;

    @Value("${app.admin.default-last-name:Administrator}")
    private String defaultLastName;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Seed Roles
        Role userRole = roleRepository.findByName(RoleType.ROLE_USER).orElseGet(() -> {
            log.info("Creating default role: ROLE_USER");
            return roleRepository.save(new Role(RoleType.ROLE_USER, "Standard user permissions"));
        });

        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN).orElseGet(() -> {
            log.info("Creating default role: ROLE_ADMIN");
            return roleRepository.save(new Role(RoleType.ROLE_ADMIN, "Administrator privileges"));
        });

        // 2. Seed Default Admin User
        if (!userRepository.existsByEmailIgnoreCase(defaultAdminEmail)) {
            log.info("Creating initial system administrator: {}", defaultAdminEmail);
            User admin = new User();
            admin.setEmail(defaultAdminEmail.toLowerCase());
            admin.setPasswordHash(passwordEncoder.encode(defaultAdminPassword));
            admin.setFirstName(defaultFirstName);
            admin.setLastName(defaultLastName);
            admin.setPhoneNumber("+442079460000");
            admin.setProvider(AuthProvider.LOCAL);
            admin.setEmailVerified(true);
            admin.setEnabled(true);
            admin.setAccountNonLocked(true);

            Address adminAddress = new Address(
                    "10 Downing Street",
                    "Westminster",
                    "London",
                    "Greater London",
                    "SW1A 2AA",
                    "United Kingdom",
                    51.503364,
                    -0.127625
            );
            admin.setAddress(adminAddress);

            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);
            log.info("Default system administrator seeded successfully with email: {}", defaultAdminEmail);
        }
    }
}
