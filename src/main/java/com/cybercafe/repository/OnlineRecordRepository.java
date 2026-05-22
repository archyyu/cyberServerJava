package com.cybercafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cybercafe.model.OnlineRecord;

public interface OnlineRecordRepository extends JpaRepository<OnlineRecord, Long> {
    
}
