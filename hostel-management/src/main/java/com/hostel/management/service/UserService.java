package com.hostel.management.service;

import com.hostel.management.model.User;
import com.hostel.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new user (Owner or Visitor).
     */
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered: " + user.getEmail());
        }
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered: " + user.getPhoneNumber());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Get the logged-in user by email or phone number.
     */
    public Optional<User> findByEmailOrPhone(String emailOrPhone) {
        return userRepository.findByEmailOrPhoneNumber(emailOrPhone, emailOrPhone);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByType(User.UserType type) {
        return userRepository.findByUserType(type);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void blockUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(User.UserStatus.BLOCKED);
            userRepository.save(user);
        });
    }

    public void unblockUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(User.UserStatus.ACTIVE);
            userRepository.save(user);
        });
    }

    public long countOwners() {
        return userRepository.countByUserType(User.UserType.OWNER);
    }

    public long countVisitors() {
        return userRepository.countByUserType(User.UserType.VISITOR);
    }
}
