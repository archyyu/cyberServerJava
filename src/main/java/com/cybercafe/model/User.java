package com.cybercafe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
@Table(name = "netbar_user")
public class User {
    @Id
    private Long userid;
    
    private Long gid;
    private String account;
    private String password;
    private String salt;
    private String token;

}
