package com.impactdryer.deviceapi.devicemanagment.application;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

public record RegisterDeviceCommand(String deviceType, String macAddress, String uplinkMacAddress) {

    public RegisterDeviceCommand {
        Preconditions.checkArgument(StringUtils.isNotEmpty(deviceType), "Device type must not be empty");
        Preconditions.checkArgument(StringUtils.isNotEmpty(macAddress), "MAC address must not be empty");
    }
}
