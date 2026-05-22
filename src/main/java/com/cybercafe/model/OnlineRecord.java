package com.cybercafe.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import lombok.Data;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@Entity
@Table(name = "netbar_online")
public class OnlineRecord {
    
    @Id
    private Long onlineID;
    private Long memberID;
    private Long machineID;
    private String machineName;
    private Integer areaID;
    private Integer ruleID;
    private String areaName;
    private Integer tariffType;
    private LocalDateTime onlineStartTime;
    private LocalDateTime offLineTime;
    private Integer internetTime;
    private Double onlineFee;
    private Long onlineRoomID;
    private Long gid;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime theDate;
    private Short ratioCostBase;
    private Short ratioCostAward;
    private Double discount;
    private LocalDateTime periodStartTime;
    private LocalDateTime periodEndTime;
    private LocalDateTime lastCostTimestamp;
    private LocalDateTime nextCostTimestamp;
    private LocalDateTime maxEndTimestamp;
    private Double allHadCost;
    private Integer ignoreTime;
    private Double startPrice;
    private Double hourPrice;
    private Integer wholeTimestamp;
    private Double startCost;
    private Short checkStart;
    private Short periodOrder;
    private Short roomOwner;

}
