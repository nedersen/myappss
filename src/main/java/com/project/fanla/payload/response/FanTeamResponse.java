package com.project.fanla.payload.response;

import com.project.fanla.model.entity.Team;

public class FanTeamResponse {
    
    private Long id;
    private String name;
    private String logoUrl;
    private String stadiumName;
    private String stadiumLocation;
    private Long countryId;
    private String countryName;
    private Boolean isActive;
    
    // Constructors
    public FanTeamResponse() {
    }
    
    public FanTeamResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.logoUrl = team.getLogoUrl();
        this.stadiumName = team.getStadiumName();
        this.stadiumLocation = team.getStadiumLocation();
        
        if (team.getCountry() != null) {
            this.countryId = team.getCountry().getId();
            this.countryName = team.getCountry().getName();
        }
        
        this.isActive = team.getIsActive();
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
