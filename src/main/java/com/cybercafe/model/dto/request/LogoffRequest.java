package com.cybercafe.model.dto.request;

public record LogoffRequest(
    Long gid,
    Long memberId,
    Boolean isFromCashier,
    Boolean force,
    Boolean isNoteClient
) {}
