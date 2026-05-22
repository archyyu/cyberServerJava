package com.cybercafe.model.logic;

import lombok.Data;

@Data
public class ActiveData {
    private String account;
    private String pcName = "";
    private Integer costType = 1; // 1 = WEEK by default assuming COST_TYPE_WEEK
    private Long areaTypeId = 0L;
    private Boolean roomOwner = false;
    private Long ruleId = 0L;
    private Float ruleValue = 0f;
    private Float periodStartTime = 0f;
    private Float periodEndTime = 0f;
    private Long payWay = 1L; // 1 = ACCOUNT
    private Float cashBalance = 0f;
    private Long cashierId = 0L;
    private Long memberId = 0L;
}
