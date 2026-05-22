package com.cybercafe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "netbar_member")
public class Member {
    @Id
    private Long memberId;
    private String account;
    private String memberName;
    private String birthDay;
    private Integer sex;
    private String password;
    private String phone;
    private String qq;
    private String openID;
    private Byte certificateType;
    private String identifyPath;
    private String certificateNum;
    private Long memberType;
    private Integer lastUpdate;
    private Integer gid;
    private Byte proviceID;
    private Integer cityID;
    private Integer districtID;
    private String address;
    private Double baseBalance;
    private Double awardBalance;
    private Integer shopId;
    private Integer onlineState;
    private String onlineArea;
    private String onlineMachine;
    private String lastUpdateDate;
}
