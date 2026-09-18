package com.resqnepal.model.enums;

public enum Severity {
    CRITICAL(4),
    HIGH(3),
    MEDIUM(2),
    LOW(1);
    
    private final int level;
    
    Severity(int level) {
        this.level = level;
    }
    
    public int getLevel() {
        return level;
    }
}
