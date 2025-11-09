package com.impactdryer.deviceapi.devicemanagment.domain;

import lombok.Getter;

@Getter
public class DeviceRegistration {
    private final MacAddress deviceMacAddress;
    private final DeviceType deviceType;
    private final MacAddress uplinkMacAddress;

    public DeviceRegistration(MacAddress deviceMacAddress, DeviceType deviceType, MacAddress uplinkMacAddress) {
        this.deviceMacAddress = deviceMacAddress;
        this.deviceType = deviceType;
        this.uplinkMacAddress = uplinkMacAddress;
    }

    public DeviceRegistration(MacAddress deviceMacAddress, DeviceType deviceType) {
        this(deviceMacAddress, deviceType, null);
    }

    public static DeviceRegistration withUplink(
            MacAddress deviceMacAddress, DeviceType deviceType, MacAddress uplinkMacAddress) {
        return new DeviceRegistration(deviceMacAddress, deviceType, uplinkMacAddress);
    }

    public static DeviceRegistration withoutUplink(MacAddress deviceMacAddress, DeviceType deviceType) {
        return new DeviceRegistration(deviceMacAddress, deviceType);
    }
}
