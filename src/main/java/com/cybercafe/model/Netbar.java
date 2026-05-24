package com.cybercafe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
@Table(name = "netbar_info")
public class Netbar {
    
    private Long gid ;
    private String name ;
    private Integer isChain ;
    
    private Integer RatioBase ;
    private Integer RatioAward ;

    private float GirlRate ;

    private Integer LockTime ;
    private Integer ActiveTime ;
    private Integer ReLogin ;
    private Integer NeedActive ;
    private Integer SubmitAction ;
    private Integer DurationAction ;
    private Integer PeriodAction ;


}
