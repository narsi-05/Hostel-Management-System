package com.hostel.management.repository;

import com.hostel.management.model.Review;
import com.hostel.management.model.Hostel;
import com.hostel.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByHostel(Hostel hostel);

    List<Review> findByVisitor(User visitor);

    Optional<Review> findByHostelAndVisitor(Hostel hostel, User visitor);

    boolean existsByHostelAndVisitor(Hostel hostel, User visitor);
}
