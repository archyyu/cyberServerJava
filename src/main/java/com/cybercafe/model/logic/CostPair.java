package com.cybercafe.model.logic;

public record CostPair(int costBase, int costAward, int costCash) {
    public int all() {
        return costBase + costAward + costCash;
    }
}
