package com.impactdryer.deviceapi.devicemanagment.web;

import com.impactdryer.deviceapi.devicemanagment.application.*;
import com.impactdryer.deviceapi.devicemanagment.application.commands.GetDeviceByMacCommand;
import com.impactdryer.deviceapi.devicemanagment.application.commands.RegisterDeviceCommand;
import com.impactdryer.deviceapi.devicemanagment.application.handlers.GetDevicesHandler;
import com.impactdryer.deviceapi.devicemanagment.application.handlers.RegisterDeviceHandler;
import com.impactdryer.deviceapi.infrastructure.openapi.DevicesApiDelegate;
import com.impactdryer.deviceapi.infrastructure.openapi.model.DeviceRegistrationRequest;
import com.impactdryer.deviceapi.infrastructure.openapi.model.DeviceSummary;
import com.impactdryer.deviceapi.infrastructure.openapi.model.DeviceType;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
class DevicesApiDelegateImpl implements DevicesApiDelegate {
    private final RegisterDeviceHandler registerDeviceHandler;
    private final GetDevicesHandler getDevicesHandler;

    @Override
    public ResponseEntity<DeviceSummary> getDeviceByMac(String macAddress) {
        DeviceDTO handle = getDevicesHandler.handle(new GetDeviceByMacCommand(macAddress));
        return ResponseEntity.ok(
                new DeviceSummary(DeviceType.fromValue(handle.getDeviceType()), handle.getMacAddress()));
    }

    @Override
    public ResponseEntity<List<DeviceSummary>> listDevices() {
        List<DeviceSummary> summaries = getDevicesHandler.getSortedDevices().stream()
                .map(dto -> new DeviceSummary(DeviceType.fromValue(dto.getDeviceType()), dto.getMacAddress()))
                .toList();
        return ResponseEntity.ok(summaries);
    }

    @Override
    public ResponseEntity<DeviceSummary> registerDevice(DeviceRegistrationRequest deviceRegistrationRequest) {
        registerDeviceHandler.registerDevice(new RegisterDeviceCommand(
                deviceRegistrationRequest.getDeviceType().getValue(),
                deviceRegistrationRequest.getMacAddress(),
                deviceRegistrationRequest.getUplinkMacAddress()));
        return ResponseEntity.created(URI.create("/devices/%s".formatted(deviceRegistrationRequest.getMacAddress())))
                .body(new DeviceSummary(
                        deviceRegistrationRequest.getDeviceType(), deviceRegistrationRequest.getMacAddress()));
    }
}
