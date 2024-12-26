package com.stockmanagement.model.enums;

public enum Unit {
    ADET("Piece"),
    KG("Kg"),
    LITRE("Liters"),
    METRE("Meters");

    private final String label;

    Unit(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
