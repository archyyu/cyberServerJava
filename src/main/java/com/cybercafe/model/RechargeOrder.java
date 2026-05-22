package com.cybercafe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "recharge_order")
public class RechargeOrder {
    @Id
    private Long rechargeOrderID;
    private Integer rechargeCompaignID;
    private Long memberID;
    private Byte rechargeWay;
    private Byte rechargeType;
    private BigDecimal cashBalance;
    private BigDecimal rechargeFee;
    private BigDecimal awardFee;
    private Byte state;
    private Integer posAccount;
    private LocalDateTime rechargeDate;
    private Integer dataVersion;
    private Byte rechargeSource;
    private Integer gid;
    private LocalDateTime lastUpdateDate;
    private Long oldRechargeOrderID;
    private Integer orderSource;
    private Integer eventID;
}
