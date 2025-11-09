package com.impactdryer.deviceapi.devicemanagment.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class MacAddress {

    // Regular expression to validate MAC address format
    // other formats ???
    public static final String MAC_ADDRESS_REGEX = "^([0-9A-Fa-f]{2}:){5}([0-9A-Fa-f]{2})$";
    private final String value;

    public MacAddress(String value) {
        if (value == null || !value.matches(MAC_ADDRESS_REGEX)) {
            throw new InvalidMacAddressException("Invalid MAC address format");
        }
        this.value = value;
    }

    public static MacAddress of(String value) {
        return new MacAddress(value);
    }

    public String value() {
        return value;
    }
}
