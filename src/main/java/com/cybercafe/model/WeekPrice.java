package com.cybercafe.model;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@Entity
@Table(name = "netbar_weekprice")
public class WeekPrice {
    @Id
    private Long ruleId;
    private Long areaId;
    private Long memberType;
    private Long ignoreTime;
    private Float startPrice;
    private Float minCostPrice;
    private String price;
}
