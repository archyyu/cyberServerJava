package com.cybercafe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "netbar_billing")
public class Billing {
    @Id
    private Long billingID;
    private Long memberID;
    private Long onlineID;
    private Long gid;
    private Integer tariffConfigID;
    private Integer tariffDataVersion;
    
    private Double currentCostBase;
    private Double currentCostAward;
    private Double currentCostTemp;

    private LocalDateTime currentCostTimestamp;
    
}
