package com.impactdryer.deviceapi.devicemanagment.application.handlers.impl;

import com.impactdryer.deviceapi.devicemanagment.application.DeviceDTO;
import com.impactdryer.deviceapi.devicemanagment.application.commands.GetDeviceByMacCommand;
import com.impactdryer.deviceapi.devicemanagment.application.handlers.GetDevicesHandler;
import com.impactdryer.deviceapi.devicemanagment.domain.DeviceRegistration;
import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;
import com.impactdryer.deviceapi.devicemanagment.infrastructure.DeviceInfrastructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetDevicesHandlerImpl implements GetDevicesHandler {
    private final DeviceInfrastructureService deviceInfrastructureService;

    @Override
    public DeviceDTO handle(GetDeviceByMacCommand command) {
        DeviceRegistration deviceByMac =
                deviceInfrastructureService.getDeviceByMac(MacAddress.of(command.macAddress()));
        return new DeviceDTO(
                deviceByMac.getDeviceMacAddress().value(),
                deviceByMac.getDeviceType().name());
    }

    @Override
    public List<DeviceDTO> getSortedDevices() {
        return List.of();
    }
}
