package com.cybercafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cybercafe.model.DurationPrice;

public interface DurationPriceRepository extends JpaRepository<DurationPrice, Long> {
    
}
