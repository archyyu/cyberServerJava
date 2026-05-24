package com.cybercafe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "netbar_billing")
public class Billing {
    @Id
    private Long billingID;
    private Long memberID;
    private Long onlineID;
    private Integer ruleType;
    private Long ruleId;
    private Long gid;
    
    private Integer currentCostBase;
    private Integer currentCostAward;
    private Integer currentCostTemp;

    private LocalDateTime currentCostTimestamp;
    
}
