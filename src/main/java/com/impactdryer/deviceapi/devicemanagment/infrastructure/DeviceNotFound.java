package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;

public class DeviceNotFound extends RuntimeException {
    public DeviceNotFound(MacAddress macAddress) {
        super("Device with MAC Address " + macAddress.getValue() + " not found.");
    }
}
