package com.cybercafe.model.logic;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class SurfUser {
    private Long memberId = 0L;
    private String account = "";
    private String pcName = "";
    private String memberName = "";
    private String password = "";
    private Long cashierID = 0L;
    private Long payWay = 1L;

    private Long gid = 0L;
    private Long onlineID = 0L;
    private Integer sex = 1;
    
    private Long areaTypeId = 0L;
    private Long memberTypeId = 0L;
    private Integer costType = 1; // 1 = Free
    
    private Float tempBalance = 0f;
    private Float baseBalance = 0f;
    private Float awardBalance = 0f;
    
    private Float discount = 1f;
    private Long logonTimestamp = 0L;
    private Long nextCostTimestamp = 0L;
    
    // Core Logic fields
    private Long ruleId = 0L;
    private Float ruleValue = 0f;
    private Long durationTime = 0L;
    private Float periodStartTime = 0f;
    private Float periodEndTime = 0f;
    
    private Float startPrice = 0f;
    private Float hourPrice = 0f;
    private Float minCostPrice = 0f;
    private Float extraCharge = 0f;
    
    private Float allHadCost = 0f;
    private Long ignoreTime = 0L;
    private Boolean roomOwner = false;
    private Short ratioCostBase = 1;
    private Short ratioCostAward = 1;
    
    private List<Object> billList = new ArrayList<>();
    
    public Float remain() {
        return tempBalance + baseBalance + awardBalance;
    }
}
