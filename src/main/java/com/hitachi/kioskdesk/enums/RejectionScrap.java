package com.hitachi.kioskdesk.enums;

/**
 * Shiva Created on 04/01/22
 */
public enum RejectionScrap {

    Rejection("Rejected"),
    Scrap("Scrap");

    private final String displayValue;

    RejectionScrap(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

}

