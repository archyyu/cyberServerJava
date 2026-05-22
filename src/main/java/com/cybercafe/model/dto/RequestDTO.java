package com.cybercafe.model.dto;

import lombok.Data;
import java.util.Map;

@Data
public class RequestDTO {
    private String fn;
    private String tm;
    private String token;
    private String from;
    private Map<String, Object> data;
    
    public boolean isFromCashier() {
        return "cashier".equalsIgnoreCase(from);
    }
}
