package com.project.fanla.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CountryRequest {
    
    @NotBlank(message = "Country name is required")
    private String name;
    
    private String logoUrl;
    
    @Size(max = 3, message = "Short code must be maximum 3 characters")
    private String shortCode;
    
    private String description;
    
    // Constructors
    public CountryRequest() {
    }
    
    public CountryRequest(String name, String logoUrl, String shortCode, String description) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.shortCode = shortCode;
        this.description = description;
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
}
