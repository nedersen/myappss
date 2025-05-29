package com.project.fanla.repository;

import com.project.fanla.model.entity.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionTypeRepository extends JpaRepository<SubscriptionType, Long> {
    Optional<SubscriptionType> findByName(String name);
    boolean existsByName(String name);
    List<SubscriptionType> findByNameContainingIgnoreCase(String name);
    List<SubscriptionType> findByIsActive(Boolean isActive);
}
