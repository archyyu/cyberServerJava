package com.cybercafe.model.dto.request;

public record ActiveUserRequest(
    Long gid,
    Long memberId,
    Long areaId,
    String machineName,
    Long durationId,
    Long periodId
) {}
