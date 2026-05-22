package com.cybercafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cybercafe.model.PeriodPrice;

public interface PeriodPriceRepository extends JpaRepository<PeriodPrice, Long> {
    
}
