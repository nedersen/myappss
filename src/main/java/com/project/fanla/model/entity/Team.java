package com.project.fanla.model.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.LocalDateTime;

@Entity
@Table(name = "teams")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Team {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "stadium_name", nullable = false)
    private String stadiumName;

    @Column(name = "stadium_location", nullable = false)
    private String stadiumLocation;
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    @JsonIdentityReference(alwaysAsId = true)
    private User createdBy;
    
    @ManyToOne
    @JoinColumn(name = "subscription_type_id")
    @JsonIdentityReference(alwaysAsId = true)
    private SubscriptionType subscriptionType;
    
    @ManyToOne
    @JoinColumn(name = "country_id")
    @JsonIdentityReference(alwaysAsId = true)
    private Country country;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "subscription_start")
    private LocalDateTime subscriptionStart;
    
    @Column(name = "subscription_expiry")
    private LocalDateTime subscriptionExpiry;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Team() {
    }
    
    public Team(String name, String logoUrl, String stadiumName, String stadiumLocation, User createdBy, 
               SubscriptionType subscriptionType, Country country, Boolean isActive, 
               LocalDateTime subscriptionStart, LocalDateTime subscriptionExpiry) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.stadiumName = stadiumName;
        this.stadiumLocation = stadiumLocation;
        this.createdBy = createdBy;
        this.subscriptionType = subscriptionType;
        this.country = country;
        this.isActive = isActive;
        this.subscriptionStart = subscriptionStart;
        this.subscriptionExpiry = subscriptionExpiry;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLogoUrl() {
        return logoUrl;
    }
    
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
    
    public String getStadiumName() {
        return stadiumName;
    }
    
    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }
    
    public String getStadiumLocation() {
        return stadiumLocation;
    }
    
    public void setStadiumLocation(String stadiumLocation) {
        this.stadiumLocation = stadiumLocation;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }
    
    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
    
    public Country getCountry() {
        return country;
    }
    
    public void setCountry(Country country) {
        this.country = country;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getSubscriptionStart() {
        return subscriptionStart;
    }
    
    public void setSubscriptionStart(LocalDateTime subscriptionStart) {
        this.subscriptionStart = subscriptionStart;
    }
    
    public LocalDateTime getSubscriptionExpiry() {
        return subscriptionExpiry;
    }
    
    public void setSubscriptionExpiry(LocalDateTime subscriptionExpiry) {
        this.subscriptionExpiry = subscriptionExpiry;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
