package com.hitachi.kioskdesk.enums;

/**
 * Shiva Created on 05/01/22
 */
public enum Role {

    ROLE_ADMIN("Admin"), ROLE_QC("Qc"), ROLE_OPERATOR("Operator");

    private final String displayValue;

    Role(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
