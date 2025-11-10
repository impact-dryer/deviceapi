package com.impactdryer.deviceapi.devicemanagment.application.handlers.impl;

import com.impactdryer.deviceapi.devicemanagment.application.handlers.TopologyHandler;
import com.impactdryer.deviceapi.devicemanagment.domain.DeviceNode;
import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;
import com.impactdryer.deviceapi.devicemanagment.infrastructure.DeviceInfrastructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TopologyHandlerImpl implements TopologyHandler {
    private final DeviceInfrastructureService deviceInfrastructureService;

    @Override
    public DeviceNode getDeviceTreeRoot() {
        return deviceInfrastructureService.getTreeRoot();
    }

    @Override
    public DeviceNode getDeviceTreeRoot(String macAddress) {
        return deviceInfrastructureService.getTreeRootedAt(MacAddress.of(macAddress));
    }
}
