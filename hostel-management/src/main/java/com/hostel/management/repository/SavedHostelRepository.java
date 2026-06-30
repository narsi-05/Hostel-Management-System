package com.hostel.management.repository;

import com.hostel.management.model.SavedHostel;
import com.hostel.management.model.Hostel;
import com.hostel.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedHostelRepository extends JpaRepository<SavedHostel, Long> {

    List<SavedHostel> findByVisitor(User visitor);

    boolean existsByVisitorAndHostel(User visitor, Hostel hostel);

    Optional<SavedHostel> findByVisitorAndHostel(User visitor, Hostel hostel);

    void deleteByVisitorAndHostel(User visitor, Hostel hostel);
}
