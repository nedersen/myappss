package com.project.fanla.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TeamRequest {
    
    @NotBlank(message = "Team name is required")
    private String name;
    
    private String logoUrl;
    
    @NotBlank(message = "Stadium name is required")
    private String stadiumName;
    
    @NotBlank(message = "Stadium location is required")
    private String stadiumLocation;
    
    @NotNull(message = "Country ID is required")
    private Long countryId;
    
    private Long subscriptionTypeId;
    
    private Boolean isActive = true;
    
    // Constructors
    public TeamRequest() {
    }
    
    public TeamRequest(String name, String logoUrl, String stadiumName, String stadiumLocation, 
                      Long countryId, Long subscriptionTypeId, Boolean isActive) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.stadiumName = stadiumName;
        this.stadiumLocation = stadiumLocation;
        this.countryId = countryId;
        this.subscriptionTypeId = subscriptionTypeId;
        this.isActive = isActive;
    }
    
    // Getters and Setters
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
    
    public Long getSubscriptionTypeId() {
        return subscriptionTypeId;
    }
    
    public void setSubscriptionTypeId(Long subscriptionTypeId) {
        this.subscriptionTypeId = subscriptionTypeId;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
