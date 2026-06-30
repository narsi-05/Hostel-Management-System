package com.hostel.management.service;

import com.hostel.management.model.User;
import com.hostel.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Custom UserDetailsService.
 * Handles authentication for Admin, Owner, and Visitor — all with hardcoded defaults.
 *
 * Default credentials (always work, even if DB is empty):
 *   ADMIN   → admin@hostel.com   / admin123
 *   OWNER   → owner@hostel.com   / owner123
 *   VISITOR → visitor@hostel.com / visitor123
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // ── Default ADMIN (hardcoded) ──────────────────────────────
    private static final String ADMIN_EMAIL    = "admin@hostel.com";
    private static final String ADMIN_PHONE    = "0000000000";
    private static final String ADMIN_PASSWORD = "$2a$10$Oki/LjYMZSDQGWvGERpPCukCcfXjlhKnpE0gmHkQUwpWrUkPRBhNm"; // admin123

    // ── Default OWNER (hardcoded) ──────────────────────────────
    private static final String OWNER_EMAIL    = "owner@hostel.com";
    private static final String OWNER_PHONE    = "9876543210";
    private static final String OWNER_PASSWORD = "$2a$10$1MK4ExEKAHe3xaG8GqhS0eTS5VcRN.YvzqwR9cUtjWrVWLK0trwtS"; // owner123

    // ── Default VISITOR (hardcoded) ────────────────────────────
    private static final String VISITOR_EMAIL    = "visitor@hostel.com";
    private static final String VISITOR_PHONE    = "9876543211";
    private static final String VISITOR_PASSWORD = "$2a$10$epps8K1SOWSJ53EY1MJX/O7P2DM5i.AacL.WKwLz5Xk.lN51yRYMe"; // visitor123

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {

        // ── 1. Default Admin ──────────────────────────────────
        if (emailOrPhone.equals(ADMIN_EMAIL) || emailOrPhone.equals(ADMIN_PHONE)) {
            return new org.springframework.security.core.userdetails.User(
                emailOrPhone, ADMIN_PASSWORD,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // ── 2. Default Owner ──────────────────────────────────
        if (emailOrPhone.equals(OWNER_EMAIL) || emailOrPhone.equals(OWNER_PHONE)) {
            // Try DB first (so DB profile is used if seeded); fall back to hardcoded
            return userRepository.findByEmailOrPhoneNumber(emailOrPhone, emailOrPhone)
                .filter(u -> u.getStatus() != User.UserStatus.BLOCKED)
                .map(u -> buildUserDetails(emailOrPhone, u.getPassword(), "ROLE_OWNER"))
                .orElseGet(() -> buildUserDetails(emailOrPhone, OWNER_PASSWORD, "ROLE_OWNER"));
        }

        // ── 3. Default Visitor ────────────────────────────────
        if (emailOrPhone.equals(VISITOR_EMAIL) || emailOrPhone.equals(VISITOR_PHONE)) {
            return userRepository.findByEmailOrPhoneNumber(emailOrPhone, emailOrPhone)
                .filter(u -> u.getStatus() != User.UserStatus.BLOCKED)
                .map(u -> buildUserDetails(emailOrPhone, u.getPassword(), "ROLE_VISITOR"))
                .orElseGet(() -> buildUserDetails(emailOrPhone, VISITOR_PASSWORD, "ROLE_VISITOR"));
        }

        // ── 4. Any other registered user (from DB) ────────────
        User user = userRepository.findByEmailOrPhoneNumber(emailOrPhone, emailOrPhone)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found with email/phone: " + emailOrPhone));

        if (user.getStatus() == User.UserStatus.BLOCKED) {
            throw new UsernameNotFoundException("Account is blocked. Contact admin.");
        }

        String role = "ROLE_" + user.getUserType().name();
        return buildUserDetails(emailOrPhone, user.getPassword(), role);
    }

    private UserDetails buildUserDetails(String username, String password, String role) {
        return new org.springframework.security.core.userdetails.User(
            username, password,
            List.of(new SimpleGrantedAuthority(role))
        );
    }
}
