package com.cybercafe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "netbar_machine")
public class Machine {
    @Id
    private Long machineID;
    private String machineName;
    private Integer areaId;
    private Integer state;
    private String mac;
    private String ip;
    private String ipMask;
    private Long gid;
}
