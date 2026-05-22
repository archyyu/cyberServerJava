package com.cybercafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cybercafe.model.Area;

public interface AreaRepository extends JpaRepository<Area, Long>{
    
}
