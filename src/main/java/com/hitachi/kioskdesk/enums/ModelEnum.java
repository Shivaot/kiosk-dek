package com.hitachi.kioskdesk.enums;

/**
 * Shiva Created on 04/01/22
 */
public enum ModelEnum {

    Model1("Model 1"),
    Model2("Model 2"),
    Model3("Model 3");

    private final String displayValue;

    ModelEnum(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
