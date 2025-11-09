package com.impactdryer.deviceapi.devicemanagment.application.handlers.impl;

import com.impactdryer.deviceapi.devicemanagment.application.commands.RegisterDeviceCommand;
import com.impactdryer.deviceapi.devicemanagment.application.handlers.RegisterDeviceHandler;
import com.impactdryer.deviceapi.devicemanagment.domain.DeviceRegistration;
import com.impactdryer.deviceapi.devicemanagment.domain.DeviceType;
import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;
import com.impactdryer.deviceapi.devicemanagment.infrastructure.DeviceInfrastructureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegisterDeviceHandlerImpl implements RegisterDeviceHandler {
    private final DeviceInfrastructureService deviceInfrastructureService;

    @Override
    public Long registerDevice(RegisterDeviceCommand command) {
        log.info("Registering device with MAC address: {}", command.macAddress());
        DeviceRegistration deviceRegistration;

        if (command.uplinkMacAddress() == null) {
            deviceRegistration = DeviceRegistration.withoutUplink(
                    MacAddress.of(command.macAddress()), DeviceType.valueOf(command.deviceType()));
        } else {
            deviceRegistration = DeviceRegistration.withUplink(
                    MacAddress.of(command.macAddress()),
                    DeviceType.valueOf(command.deviceType()),
                    MacAddress.of(command.uplinkMacAddress()));
        }

        return deviceInfrastructureService.registerDevice(deviceRegistration);
    }
}
