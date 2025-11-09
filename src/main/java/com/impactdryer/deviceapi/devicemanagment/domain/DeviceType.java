package com.impactdryer.deviceapi.devicemanagment.domain;

public enum DeviceType {
    GATEWAY("Gateway"),
    SWITCH("Switch"),
    ACCESS_POINT("Access Point");
    private final String value;

    DeviceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
