package com.hostel.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Hostel entity representing a hostel registered by an Owner.
 */
@Entity
@Table(name = "hostels")
public class Hostel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Hostel name is required")
    @Column(name = "hostel_name", nullable = false)
    private String hostelName;

    @Enumerated(EnumType.STRING)
    @Column(name = "hostel_type", nullable = false)
    private HostelType hostelType;

    @NotBlank(message = "Location is required")
    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false)
    private Availability availability;

    @Enumerated(EnumType.STRING)
    @Column(name = "sharing_type", nullable = false)
    private SharingType sharingType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Column(name = "amount_per_month", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPerMonth;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "photos", columnDefinition = "TEXT")
    private String photos; // Comma-separated file paths

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private HostelStatus status = HostelStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SavedHostel> savedByUsers;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enums
    public enum HostelType {
        GIRLS_HOSTEL("Girls Hostel"),
        BOYS_HOSTEL("Boys Hostel"),
        MIXED("Mixed (Co-ed)");

        private final String displayName;
        HostelType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum Availability {
        STUDENTS_ONLY("Only for Students"),
        ANYONE("Anyone");

        private final String displayName;
        Availability(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum SharingType {
        TWO_SHARING("2 Sharing"),
        THREE_SHARING("3 Sharing"),
        FOUR_SHARING("4 Sharing"),
        SINGLE("Single Room");

        private final String displayName;
        SharingType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public enum HostelStatus {
        ACTIVE, INACTIVE, PENDING_REVIEW
    }

    // Helper method
    public String[] getPhotoArray() {
        if (photos == null || photos.isEmpty()) return new String[0];
        return photos.split(",");
    }

    // Constructors
    public Hostel() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }

    public HostelType getHostelType() { return hostelType; }
    public void setHostelType(HostelType hostelType) { this.hostelType = hostelType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Availability getAvailability() { return availability; }
    public void setAvailability(Availability availability) { this.availability = availability; }

    public SharingType getSharingType() { return sharingType; }
    public void setSharingType(SharingType sharingType) { this.sharingType = sharingType; }

    public BigDecimal getAmountPerMonth() { return amountPerMonth; }
    public void setAmountPerMonth(BigDecimal amountPerMonth) { this.amountPerMonth = amountPerMonth; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhotos() { return photos; }
    public void setPhotos(String photos) { this.photos = photos; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }

    public HostelStatus getStatus() { return status; }
    public void setStatus(HostelStatus status) { this.status = status; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public List<SavedHostel> getSavedByUsers() { return savedByUsers; }
    public void setSavedByUsers(List<SavedHostel> savedByUsers) { this.savedByUsers = savedByUsers; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
