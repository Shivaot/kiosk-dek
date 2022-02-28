package com.hitachi.kioskdesk.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Shiva Created on 04/01/22
 */
public enum RejectionScrap {

    Rejection("Rejected"),
    Scrap("Scrap"),
    Rework("Rework");

    private final String displayValue;

    RejectionScrap(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    private static final Map<String, RejectionScrap> BY_LABEL = new HashMap<>();

    static {
        for (RejectionScrap e: values()) {
            BY_LABEL.put(e.displayValue, e);
        }
    }

    public static RejectionScrap valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

}

