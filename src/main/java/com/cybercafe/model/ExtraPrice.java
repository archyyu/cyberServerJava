package com.cybercafe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "netbar_extraprice")
public class ExtraPrice {
    @Id
    private Long ruleId;
    private Long areaTypeId;
    private Long memberTypeId;
    private Float additionalPrice;
}
