package com.project.fanla.controller.superadmin;

import com.project.fanla.model.entity.Country;
import com.project.fanla.payload.request.CountryRequest;
import com.project.fanla.payload.response.CountryResponse;
import com.project.fanla.repository.CountryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/superadmin/countries")
@PreAuthorize("hasRole('ROLE_SuperAdmin')")
public class CountryController {

    @Autowired
    private CountryRepository countryRepository;

    // Get all countries
    @GetMapping
    public ResponseEntity<List<CountryResponse>> getAllCountries() {
        List<CountryResponse> countries = countryRepository.findAll().stream()
                .map(CountryResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(countries);
    }

    // Get country by ID
    @GetMapping("/{id}")
    public ResponseEntity<CountryResponse> getCountryById(@PathVariable Long id) {
        return countryRepository.findById(id)
                .map(CountryResponse::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new country
    @PostMapping
    public ResponseEntity<?> createCountry(@Valid @RequestBody CountryRequest countryRequest) {
        // Check if country with the same name already exists
        if (countryRepository.existsByName(countryRequest.getName())) {
            return ResponseEntity
                    .badRequest()
                    .body(String.format("Country with name '%s' already exists", countryRequest.getName()));
        }

        // Create new country
        Country country = new Country();
        country.setName(countryRequest.getName());
        country.setLogoUrl(countryRequest.getLogoUrl());
        country.setShortCode(countryRequest.getShortCode());
        country.setDescription(countryRequest.getDescription());

        Country savedCountry = countryRepository.save(country);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CountryResponse(savedCountry));
    }

    // Update country
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCountry(@PathVariable Long id, @Valid @RequestBody CountryRequest countryRequest) {
        return countryRepository.findById(id)
                .map(country -> {
                    // Check if name is being changed and if the new name already exists
                    if (!country.getName().equals(countryRequest.getName()) && 
                            countryRepository.existsByName(countryRequest.getName())) {
                        return ResponseEntity
                                .badRequest()
                                .body(String.format("Country with name '%s' already exists", countryRequest.getName()));
                    }
                    
                    country.setName(countryRequest.getName());
                    country.setLogoUrl(countryRequest.getLogoUrl());
                    country.setShortCode(countryRequest.getShortCode());
                    country.setDescription(countryRequest.getDescription());
                    
                    Country updatedCountry = countryRepository.save(country);
                    return ResponseEntity.ok(new CountryResponse(updatedCountry));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete country
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCountry(@PathVariable Long id) {
        return countryRepository.findById(id)
                .map(country -> {
                    // Check if country has associated teams
                    if (country.getTeams() != null && !country.getTeams().isEmpty()) {
                        return ResponseEntity
                                .badRequest()
                                .body("Cannot delete country with associated teams");
                    }
                    
                    countryRepository.delete(country);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Search countries by name
    @GetMapping("/search")
    public ResponseEntity<List<CountryResponse>> searchCountries(@RequestParam String name) {
        List<CountryResponse> countries = countryRepository.findByNameContainingIgnoreCase(name).stream()
                .map(CountryResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(countries);
    }
}
