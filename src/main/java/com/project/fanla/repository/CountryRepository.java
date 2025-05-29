package com.project.fanla.repository;

import com.project.fanla.model.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByName(String name);
    List<Country> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);
}
