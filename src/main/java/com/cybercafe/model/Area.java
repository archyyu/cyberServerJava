package com.cybercafe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "netbar_area")
public class Area {
    @Id
    private Long areaId;
    private String areaName ;
    private Integer roomType ;
}
