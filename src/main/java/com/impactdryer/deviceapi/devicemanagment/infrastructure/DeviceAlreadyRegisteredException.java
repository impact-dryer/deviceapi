package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;

public class DeviceAlreadyRegisteredException extends RuntimeException {
    public DeviceAlreadyRegisteredException(MacAddress deviceMacAddress) {
        super("Device with MAC address " + deviceMacAddress.value() + " is already registered.");
    }
}
