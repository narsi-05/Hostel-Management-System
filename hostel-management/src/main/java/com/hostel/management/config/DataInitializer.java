package com.hostel.management.config;

import com.hostel.management.model.*;
import com.hostel.management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds initial demo data into the database on first startup.
 * Creates sample Owner, Visitor, and Hostel entries for testing.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private HostelRepository hostelRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Only seed if no users exist
        if (userRepository.count() > 0) return;

        System.out.println("=== Seeding demo data ===");

        // --- Sample Owner ---
        User owner = new User();
        owner.setFirstName("Rajesh");
        owner.setLastName("Kumar");
        owner.setEmail("owner@hostel.com");
        owner.setPhoneNumber("9876543210");
        owner.setPassword(passwordEncoder.encode("owner123"));
        owner.setUserType(User.UserType.OWNER);
        userRepository.save(owner);

        // --- Sample Visitor ---
        User visitor = new User();
        visitor.setFirstName("Priya");
        visitor.setLastName("Sharma");
        visitor.setEmail("visitor@hostel.com");
        visitor.setPhoneNumber("9876543211");
        visitor.setPassword(passwordEncoder.encode("visitor123"));
        visitor.setUserType(User.UserType.VISITOR);
        userRepository.save(visitor);

        // --- Sample Hostel 1 ---
        Hostel h1 = new Hostel();
        h1.setHostelName("Sunrise Boys Hostel");
        h1.setHostelType(Hostel.HostelType.BOYS_HOSTEL);
        h1.setLocation("Near Osmania University, Tarnaka");
        h1.setCity("Hyderabad");
        h1.setState("Telangana");
        h1.setAvailability(Hostel.Availability.STUDENTS_ONLY);
        h1.setSharingType(Hostel.SharingType.THREE_SHARING);
        h1.setAmountPerMonth(new BigDecimal("4500"));
        h1.setPhoneNumber("9876543210");
        h1.setDescription("Clean and comfortable boys hostel near Osmania University. " +
                "24/7 security, Wi-Fi, laundry, and mess facility available. " +
                "Walking distance to bus stop.");
        h1.setAverageRating(4.2);
        h1.setTotalReviews(5);
        h1.setOwner(owner);
        hostelRepository.save(h1);

        // --- Sample Hostel 2 ---
        Hostel h2 = new Hostel();
        h2.setHostelName("Green Valley Girls Hostel");
        h2.setHostelType(Hostel.HostelType.GIRLS_HOSTEL);
        h2.setLocation("Himayat Nagar, Near St. Francis College");
        h2.setCity("Hyderabad");
        h2.setState("Telangana");
        h2.setAvailability(Hostel.Availability.ANYONE);
        h2.setSharingType(Hostel.SharingType.TWO_SHARING);
        h2.setAmountPerMonth(new BigDecimal("6000"));
        h2.setPhoneNumber("9876543210");
        h2.setDescription("Safe and comfortable girls hostel. " +
                "CCTV surveillance, gated community, home-cooked meals, and peaceful environment. " +
                "Ideal for students and working women.");
        h2.setAverageRating(4.6);
        h2.setTotalReviews(8);
        h2.setOwner(owner);
        hostelRepository.save(h2);

        // --- Sample Hostel 3 ---
        Hostel h3 = new Hostel();
        h3.setHostelName("City Stay PG");
        h3.setHostelType(Hostel.HostelType.MIXED);
        h3.setLocation("Ameerpet, Near Metro Station");
        h3.setCity("Hyderabad");
        h3.setState("Telangana");
        h3.setAvailability(Hostel.Availability.ANYONE);
        h3.setSharingType(Hostel.SharingType.SINGLE);
        h3.setAmountPerMonth(new BigDecimal("8500"));
        h3.setPhoneNumber("9876543210");
        h3.setDescription("Premium co-ed PG accommodation in central Hyderabad. " +
                "AC rooms, attached bathroom, high-speed internet, and fully furnished. " +
                "Metro station 2 minutes walk.");
        h3.setAverageRating(4.8);
        h3.setTotalReviews(12);
        h3.setOwner(owner);
        hostelRepository.save(h3);

        
    }
}
