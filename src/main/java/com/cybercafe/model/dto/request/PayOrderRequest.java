package com.cybercafe.model.dto.request;

public record PayOrderRequest(
    Long orderId,
    Float orderCost,
    Float baseCost
) {}
