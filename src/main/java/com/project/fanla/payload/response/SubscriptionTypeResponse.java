package com.project.fanla.payload.response;

import com.project.fanla.model.entity.SubscriptionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SubscriptionTypeResponse {
    
    private Long id;
    private String name;
    private Integer maxClients;
    private Integer maxMatches;
    private BigDecimal price;
    private Integer durationDays;
    private String description;
    private Boolean isActive;
    private Long createdById;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer teamsCount;
    
    // Constructors
    public SubscriptionTypeResponse() {
    }
    
    public SubscriptionTypeResponse(SubscriptionType subscriptionType) {
        this.id = subscriptionType.getId();
        this.name = subscriptionType.getName();
        this.maxClients = subscriptionType.getMaxClients();
        this.maxMatches = subscriptionType.getMaxMatches();
        this.price = subscriptionType.getPrice();
        this.durationDays = subscriptionType.getDurationDays();
        this.description = subscriptionType.getDescription();
        this.isActive = subscriptionType.getIsActive();
        
        if (subscriptionType.getCreatedBy() != null) {
            this.createdById = subscriptionType.getCreatedBy().getId();
            this.createdByUsername = subscriptionType.getCreatedBy().getUsername();
        }
        
        this.createdAt = subscriptionType.getCreatedAt();
        this.updatedAt = subscriptionType.getUpdatedAt();
        
        if (subscriptionType.getTeams() != null) {
            this.teamsCount = subscriptionType.getTeams().size();
        } else {
            this.teamsCount = 0;
        }
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
    
    public Integer getTeamsCount() {
        return teamsCount;
    }
    
    public void setTeamsCount(Integer teamsCount) {
        this.teamsCount = teamsCount;
    }
}
