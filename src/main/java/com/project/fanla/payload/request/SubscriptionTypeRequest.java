package com.project.fanla.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class SubscriptionTypeRequest {
    
    @NotBlank(message = "Subscription type name is required")
    private String name;
    
    @PositiveOrZero(message = "Max clients must be a positive number or zero")
    private Integer maxClients;
    
    @PositiveOrZero(message = "Max matches must be a positive number or zero")
    private Integer maxMatches;
    
    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be a positive number or zero")
    private BigDecimal price;
    
    @Positive(message = "Duration days must be a positive number")
    private Integer durationDays;
    
    private String description;
    
    private Boolean isActive = true;
    
    // Constructors
    public SubscriptionTypeRequest() {
    }
    
    public SubscriptionTypeRequest(String name, Integer maxClients, Integer maxMatches, 
                                  BigDecimal price, Integer durationDays, String description, Boolean isActive) {
        this.name = name;
        this.maxClients = maxClients;
        this.maxMatches = maxMatches;
        this.price = price;
        this.durationDays = durationDays;
        this.description = description;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getMaxClients() {
        return maxClients;
    }
    
    public void setMaxClients(Integer maxClients) {
        this.maxClients = maxClients;
    }
    
    public Integer getMaxMatches() {
        return maxMatches;
    }
    
    public void setMaxMatches(Integer maxMatches) {
        this.maxMatches = maxMatches;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getDurationDays() {
        return durationDays;
    }
    
    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
