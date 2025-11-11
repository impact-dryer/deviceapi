package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(MacAddress macAddress) {
        super("Device with MAC Address " + macAddress.getValue() + " not found.");
    }
}
