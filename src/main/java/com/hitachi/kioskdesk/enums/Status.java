package com.hitachi.kioskdesk.enums;

/**
 * Shiva Created on 04/01/22
 */
public enum Status {

    NEW("New"),
    QC("Qc"),
    COMPLETED("Completed");

    private final String displayValue;

    Status(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
