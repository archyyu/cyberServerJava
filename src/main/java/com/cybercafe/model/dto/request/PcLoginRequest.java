package com.cybercafe.model.dto.request;

public record PcLoginRequest(
    Long gid,
    Long memberId,
    String pcName,
    String password
) {}
