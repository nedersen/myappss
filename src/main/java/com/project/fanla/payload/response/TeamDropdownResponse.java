package com.project.fanla.payload.response;

import com.project.fanla.model.entity.Team;

public class TeamDropdownResponse {
    
    private Long id;
    private String name;
    private String countryName;
    
    // Constructors
    public TeamDropdownResponse() {
    }
    
    public TeamDropdownResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        
        if (team.getCountry() != null) {
            this.countryName = team.getCountry().getName();
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
    
    public String getCountryName() {
        return countryName;
    }
    
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
