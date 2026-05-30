package com.cybercafe.model.dto.request;

public record PcHeartbeatRequest(
    Long memberID,
    String pcName,
    String pcIp,
    String pcMac
) {}
