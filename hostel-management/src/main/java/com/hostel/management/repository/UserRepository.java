package com.hostel.management.repository;

import com.hostel.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);

    List<User> findByUserType(User.UserType userType);

    List<User> findByStatus(User.UserStatus status);

    long countByUserType(User.UserType userType);

    long countByStatus(User.UserStatus status);
}
