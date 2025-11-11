package com.impactdryer.deviceapi.devicemanagment.application.handlers;

import com.impactdryer.deviceapi.devicemanagment.application.commands.RegisterDeviceCommand;
import com.impactdryer.deviceapi.devicemanagment.domain.DeviceRegistration;

public interface RegisterDeviceHandler {
    DeviceRegistration registerDevice(RegisterDeviceCommand command);
}
