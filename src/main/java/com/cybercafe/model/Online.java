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
@Table(name = "netbar_online")
public class Online {
    @Id
    private Long onlineID;
    private Long memberID;
    private Long machineID;
    private String machineName;
    private Long areaID;
    private Integer ruleType;
    private Long ruleID;
    private String areaName;
    private Integer tariffType;
    private LocalDateTime onlineActiveTime;
    private LocalDateTime onlineStartTime;
    private LocalDateTime offLineTime;
    private Integer internetTime;
    private Double onlineFee;
    private Long onlineRoomID;
    private Long gid;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime theDate;
    private Integer allCostBase;
    private Integer allCostAward;
    private Integer allCostCash;
    private Integer discount;
    private LocalDateTime periodStartTime;
    private LocalDateTime periodEndTime;
    private LocalDateTime lastCostTimestamp;
    private LocalDateTime nextCostTimestamp;
    private LocalDateTime maxEndTimestamp;
    private Integer allHadCost;
    private Integer ignoreTime;
    private Integer startPrice;
    private Integer hourPrice;
    private Integer wholeTimestamp;
    private Float startCost;
    private Short checkStart;
    private Short periodOrder;
    private Short roomOwner;

}
