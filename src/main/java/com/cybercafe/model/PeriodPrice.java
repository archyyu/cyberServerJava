package com.cybercafe.model;

import lombok.Data;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Data
@Table(name = "netbar_periodprice")
public class PeriodPrice {
    @Id
    private Long ruleId;
    private Long areaId;
    private Long memberType;
    private Float startTime;
    private Float endTime;
    private Float price;
    private Long periodTime;
    private Long byType;
    private Long typeFlag;

    public boolean isIn(long now, boolean isSmart) {
        double beginTime = isSmart ? startTime : (startTime - 0.2);
        float nowTime = timestampToFormat(now);

        if (startTime < endTime) {
            return beginTime < nowTime && nowTime < endTime;
        } else {
            return beginTime < nowTime || nowTime < endTime;
        }
    }

    private float timestampToFormat(long curTime) {
        LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochSecond(curTime), ZoneId.systemDefault());
        return dt.getHour() + ((float) dt.getMinute() / 60);
    }
}
