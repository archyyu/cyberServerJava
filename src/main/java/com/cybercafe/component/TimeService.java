package com.cybercafe.component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;

@Component
public class TimeService {
    
    public long now() {
        return System.currentTimeMillis() / 1000;
    }

    public LocalDateTime nowLocalDateTime() {
        return Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
