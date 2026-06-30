package com.hostel.management.repository;

import com.hostel.management.model.Hostel;
import com.hostel.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long> {

    List<Hostel> findByOwner(User owner);

    List<Hostel> findByStatus(Hostel.HostelStatus status);

    List<Hostel> findByStatusAndLocationContainingIgnoreCase(Hostel.HostelStatus status, String location);

    List<Hostel> findByStatusAndCityContainingIgnoreCase(Hostel.HostelStatus status, String city);

    List<Hostel> findByStatusAndHostelType(Hostel.HostelStatus status, Hostel.HostelType hostelType);

    long countByStatus(Hostel.HostelStatus status);

    long countByOwner(User owner);
}
