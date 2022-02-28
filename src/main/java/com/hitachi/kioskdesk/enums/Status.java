package com.hitachi.kioskdesk.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Shiva Created on 04/01/22
 */
public enum Status {

    NEW("Production Done"),
    QC("QC Done"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayValue;

    Status(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    private static final Map<String, Status> BY_LABEL = new HashMap<>();

    static {
        for (Status e: values()) {
            BY_LABEL.put(e.displayValue, e);
        }
    }

    public static Status valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }
}
