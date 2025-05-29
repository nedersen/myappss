package com.project.fanla.payload.response;

import com.project.fanla.model.entity.Country;

public class CountryResponse {
    
    private Long id;
    private String name;
    private String logoUrl;
    private String shortCode;
    private String description;
    private int teamCount;
    
    // Constructors
    public CountryResponse() {
    }
    
    public CountryResponse(Country country) {
        this.id = country.getId();
        this.name = country.getName();
        this.logoUrl = country.getLogoUrl();
        this.shortCode = country.getShortCode();
        this.description = country.getDescription();
        this.teamCount = country.getTeams() != null ? country.getTeams().size() : 0;
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
    
    public String getShortCode() {
        return shortCode;
    }
    
    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getTeamCount() {
        return teamCount;
    }
    
    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }
}
