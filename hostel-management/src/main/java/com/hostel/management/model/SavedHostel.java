package com.hostel.management.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * SavedHostel entity for visitor's liked/saved hostels.
 */
@Entity
@Table(name = "saved_hostels",
       uniqueConstraints = @UniqueConstraint(columnNames = {"visitor_id", "hostel_id"}))
public class SavedHostel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitor_id", nullable = false)
    private User visitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel;

    @Column(name = "saved_at", updatable = false)
    private LocalDateTime savedAt;

    @PrePersist
    protected void onCreate() { savedAt = LocalDateTime.now(); }

    // Constructors
    public SavedHostel() {}

    public SavedHostel(User visitor, Hostel hostel) {
        this.visitor = visitor;
        this.hostel = hostel;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getVisitor() { return visitor; }
    public void setVisitor(User visitor) { this.visitor = visitor; }
    public Hostel getHostel() { return hostel; }
    public void setHostel(Hostel hostel) { this.hostel = hostel; }
    public LocalDateTime getSavedAt() { return savedAt; }
    public void setSavedAt(LocalDateTime savedAt) { this.savedAt = savedAt; }
}
