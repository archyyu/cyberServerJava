package com.cybercafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cybercafe.model.Netbar;

public interface NetbarRepository extends JpaRepository<Netbar, Long> {
    
}
