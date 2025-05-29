package com.project.fanla.payload.response;

import com.project.fanla.model.entity.Team;

import java.time.LocalDateTime;

public class TeamResponse {
    
    private Long id;
    private String name;
    private String logoUrl;
    private String stadiumName;
    private String stadiumLocation;
    private Long countryId;
    private String countryName;
    private Long subscriptionTypeId;
    private String subscriptionTypeName;
    private Long createdById;
    private String createdByUsername;
    private Boolean isActive;
    private LocalDateTime subscriptionStart;
    private LocalDateTime subscriptionExpiry;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public TeamResponse() {
    }
    
    public TeamResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.logoUrl = team.getLogoUrl();
        this.stadiumName = team.getStadiumName();
        this.stadiumLocation = team.getStadiumLocation();
        
        if (team.getCountry() != null) {
            this.countryId = team.getCountry().getId();
            this.countryName = team.getCountry().getName();
        }
        
        if (team.getSubscriptionType() != null) {
            this.subscriptionTypeId = team.getSubscriptionType().getId();
            this.subscriptionTypeName = team.getSubscriptionType().getName();
        }
        
        if (team.getCreatedBy() != null) {
            this.createdById = team.getCreatedBy().getId();
            this.createdByUsername = team.getCreatedBy().getUsername();
        }
        
        this.isActive = team.getIsActive();
        this.subscriptionStart = team.getSubscriptionStart();
        this.subscriptionExpiry = team.getSubscriptionExpiry();
        this.createdAt = team.getCreatedAt();
        this.updatedAt = team.getUpdatedAt();
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
    
    public Long getCountryId() {
        return countryId;
    }
    
    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
    
    public String getCountryName() {
        return countryName;
    }
    
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    
    public Long getSubscriptionTypeId() {
        return subscriptionTypeId;
    }
    
    public void setSubscriptionTypeId(Long subscriptionTypeId) {
        this.subscriptionTypeId = subscriptionTypeId;
    }
    
    public String getSubscriptionTypeName() {
        return subscriptionTypeName;
    }
    
    public void setSubscriptionTypeName(String subscriptionTypeName) {
        this.subscriptionTypeName = subscriptionTypeName;
    }
    
    public Long getCreatedById() {
        return createdById;
    }
    
    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }
    
    public String getCreatedByUsername() {
        return createdByUsername;
    }
    
    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
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
