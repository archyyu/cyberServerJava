package com.cybercafe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "netbar_durationprice")
public class DurationPrice {
    @Id
    private Long ruleId;
    private Long areaId;
    private Long memberType;
    private Long durationTime;
    private Integer price;
}
