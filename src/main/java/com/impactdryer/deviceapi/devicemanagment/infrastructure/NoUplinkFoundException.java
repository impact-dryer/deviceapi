package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;

public class NoUplinkFoundException extends RuntimeException {
    public NoUplinkFoundException(MacAddress macAddress) {
        super("No uplink found for device with MAC address: " + macAddress.value());
    }
}
