package com.cybercafe.model.dto.request;

public record LoginUserRequest(
    Long gid,
    String account,
    String password
) {
    
}
