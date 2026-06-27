package com.firstbank.uganda.model;

/**
 * Enumeration of First Bank Uganda branches with their codes.
 */
public enum Branch {
    KAMPALA("KLA", "Kampala"),
    GULU("GUL", "Gulu"),
    MBARARA("MBR", "Mbarara"),
    JINJA("JIN", "Jinja"),
    MBALE("MBL", "Mbale");

    private final String code;
    private final String displayName;

    Branch(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }

    @Override
    public String toString() {
        return displayName;
    }
}
