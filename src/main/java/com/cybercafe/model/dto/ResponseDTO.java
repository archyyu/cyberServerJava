package com.cybercafe.model.dto;

import lombok.Data;

@Data
public class ResponseDTO {
    private int status;
    private String message;
    private Object data;
    
    public ResponseDTO(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
    
    public static ResponseDTO success(Object data) {
        return new ResponseDTO(200, "success", data);
    }
    
    public static ResponseDTO error(String message) {
        return new ResponseDTO(500, message, null);
    }
    public static ResponseDTO error(int status, String message) {
        return new ResponseDTO(500, message, null);
    }
}
