package com.cybercafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cybercafe.model.ExtraPrice;

public interface ExtraPriceRepository extends JpaRepository<ExtraPrice, Long> {
    
}
