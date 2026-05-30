package com.cybercafe.model.dto.request;

public record ChangePcRequest(
    String pcName,
    Long areaId
) {}
