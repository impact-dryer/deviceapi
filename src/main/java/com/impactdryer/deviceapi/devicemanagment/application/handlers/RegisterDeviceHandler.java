package com.impactdryer.deviceapi.devicemanagment.application.handlers;

import com.impactdryer.deviceapi.devicemanagment.application.commands.RegisterDeviceCommand;

public interface RegisterDeviceHandler {
    Long registerDevice(RegisterDeviceCommand command);
}
