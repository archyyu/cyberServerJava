package com.cybercafe.model.types;

public enum OnlineType {
    
    WEEK(1, "weekprice"),
    PERIOD(2, "period"),
    DURATION(3, "duration"),
    END(100, "end");

    private int typeId;
    private String typeName;

    public int typeId() {
        return this.typeId;
    }

    public String typeName() {
        return this.typeName;
    }

    private OnlineType(int id, String name) {
        this.typeId = id;
        this.typeName = name;
    }

}
