package com.hostel.management.service;

import com.hostel.management.config.MvcConfig;
import com.hostel.management.model.*;
import com.hostel.management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class HostelService {

    @Autowired private HostelRepository hostelRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private SavedHostelRepository savedHostelRepository;

    @Value("${hostel.upload.dir}")
    private String uploadDir;

    /** Adds a hostel without any photo upload (photo upload UI removed from Owner forms). */
    public Hostel addHostel(Hostel hostel, User owner) throws IOException {
        return addHostel(hostel, owner, null);
    }

    public Hostel addHostel(Hostel hostel, User owner, List<MultipartFile> photos) throws IOException {
        hostel.setOwner(owner);
        // Save photos
        if (photos != null && !photos.isEmpty()) {
            List<String> photoPaths = new ArrayList<>();
            // Resolve via the SAME logic MvcConfig uses to serve files,
            // so saved files are always found by the /uploads/** handler.
            Path uploadPath = MvcConfig.resolveUploadPath(uploadDir);
            for (MultipartFile photo : photos) {
                if (!photo.isEmpty()) {
                    String filename = UUID.randomUUID() + "_" + sanitizeFilename(photo.getOriginalFilename());
                    Files.copy(photo.getInputStream(), uploadPath.resolve(filename),
                               StandardCopyOption.REPLACE_EXISTING);
                    photoPaths.add(filename);
                }
            }
            hostel.setPhotos(String.join(",", photoPaths));
        }
        return hostelRepository.save(hostel);
    }

    /** Updates a hostel without any photo upload. */
    public Hostel updateHostel(Hostel hostel) throws IOException {
        return updateHostel(hostel.getId(), hostel, null, null);
    }

    public Hostel updateHostel(Hostel hostel, List<MultipartFile> photos) throws IOException {
        return updateHostel(hostel.getId(), hostel, photos, null);
    }

    public Hostel updateHostel(Long id, Hostel updatedHostel, List<MultipartFile> photos, List<String> deletePhotos) throws IOException {
        Hostel existing = hostelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hostel not found"));

        existing.setHostelName(updatedHostel.getHostelName());
        existing.setHostelType(updatedHostel.getHostelType());
        existing.setAvailability(updatedHostel.getAvailability());
        existing.setLocation(updatedHostel.getLocation());
        existing.setCity(updatedHostel.getCity());
        existing.setState(updatedHostel.getState());
        existing.setSharingType(updatedHostel.getSharingType());
        existing.setAmountPerMonth(updatedHostel.getAmountPerMonth());
        existing.setPhoneNumber(updatedHostel.getPhoneNumber());
        existing.setDescription(updatedHostel.getDescription());

        // Process photos
        List<String> photoPaths = new ArrayList<>();
        if (existing.getPhotos() != null && !existing.getPhotos().isEmpty()) {
            photoPaths.addAll(Arrays.asList(existing.getPhotos().split(",")));
        }

        // Delete photos if requested
        if (deletePhotos != null && !deletePhotos.isEmpty()) {
            photoPaths.removeAll(deletePhotos);
        }

        // Upload and append new photos
        if (photos != null && !photos.isEmpty()) {
            Path uploadPath = MvcConfig.resolveUploadPath(uploadDir);
            for (MultipartFile photo : photos) {
                if (!photo.isEmpty()) {
                    String filename = UUID.randomUUID() + "_" + sanitizeFilename(photo.getOriginalFilename());
                    Files.copy(photo.getInputStream(), uploadPath.resolve(filename),
                               StandardCopyOption.REPLACE_EXISTING);
                    photoPaths.add(filename);
                }
            }
        }

        if (photoPaths.isEmpty()) {
            existing.setPhotos(null);
        } else {
            existing.setPhotos(String.join(",", photoPaths));
        }

        return hostelRepository.save(existing);
    }

    /** Strips path separators from the original filename to avoid path traversal / nested folders. */
    private String sanitizeFilename(String originalFilename) {
        if (originalFilename == null) return "file";
        return originalFilename.replaceAll("[\\\\/]+", "_");
    }

    public Optional<Hostel> findById(Long id) {
        return hostelRepository.findById(id);
    }

    public List<Hostel> getHostelsByOwner(User owner) {
        return hostelRepository.findByOwner(owner);
    }

    public List<Hostel> getAllActiveHostels() {
        return hostelRepository.findByStatus(Hostel.HostelStatus.ACTIVE);
    }

    public List<Hostel> getAllHostels() {
        return hostelRepository.findAll();
    }

    public List<Hostel> searchHostels(String location, Hostel.HostelType type) {
        if (location != null && !location.isEmpty() && type != null) {
            String keyword = location.toLowerCase();
            return hostelRepository.findByStatusAndHostelType(Hostel.HostelStatus.ACTIVE, type).stream()
                    .filter(h -> (h.getLocation() != null && h.getLocation().toLowerCase().contains(keyword))
                              || (h.getCity() != null && h.getCity().toLowerCase().contains(keyword)))
                    .collect(Collectors.toList());
        } else if (location != null && !location.isEmpty()) {
            String keyword = location.toLowerCase();
            return hostelRepository.findByStatus(Hostel.HostelStatus.ACTIVE).stream()
                    .filter(h -> (h.getHostelName() != null && h.getHostelName().toLowerCase().contains(keyword))
                              || (h.getLocation() != null && h.getLocation().toLowerCase().contains(keyword))
                              || (h.getCity() != null && h.getCity().toLowerCase().contains(keyword)))
                    .collect(Collectors.toList());
        } else if (type != null) {
            return hostelRepository.findByStatusAndHostelType(Hostel.HostelStatus.ACTIVE, type);
        }
        return hostelRepository.findByStatus(Hostel.HostelStatus.ACTIVE);
    }

    public void deleteHostel(Long id) {
        hostelRepository.deleteById(id);
    }

    // Reviews
    public Review addReview(Review review, User visitor, Hostel hostel) {
        review.setVisitor(visitor);
        review.setHostel(hostel);
        Review saved = reviewRepository.save(review);
        updateHostelRating(hostel);
        return saved;
    }

    private void updateHostelRating(Hostel hostel) {
        List<Review> reviews = reviewRepository.findByHostel(hostel);
        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        hostel.setAverageRating(avg);
        hostel.setTotalReviews(reviews.size());
        hostelRepository.save(hostel);
    }

    public List<Review> getHostelReviews(Hostel hostel) {
        return reviewRepository.findByHostel(hostel);
    }

    public boolean hasReviewed(User visitor, Hostel hostel) {
        return reviewRepository.existsByHostelAndVisitor(hostel, visitor);
    }

    // Saved Hostels
    public void saveHostel(User visitor, Hostel hostel) {
        if (!savedHostelRepository.existsByVisitorAndHostel(visitor, hostel)) {
            savedHostelRepository.save(new SavedHostel(visitor, hostel));
        }
    }

    public void unsaveHostel(User visitor, Hostel hostel) {
        savedHostelRepository.deleteByVisitorAndHostel(visitor, hostel);
    }

    public boolean isSaved(User visitor, Hostel hostel) {
        return savedHostelRepository.existsByVisitorAndHostel(visitor, hostel);
    }

    public List<SavedHostel> getSavedHostels(User visitor) {
        return savedHostelRepository.findByVisitor(visitor);
    }

    public long countActiveHostels() {
        return hostelRepository.countByStatus(Hostel.HostelStatus.ACTIVE);
    }
}
