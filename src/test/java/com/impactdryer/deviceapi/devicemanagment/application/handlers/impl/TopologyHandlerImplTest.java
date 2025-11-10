package com.impactdryer.deviceapi.devicemanagment.application.handlers.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;
import com.impactdryer.deviceapi.devicemanagment.infrastructure.DeviceInfrastructureService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TopologyHandlerImplTest {
    private DeviceInfrastructureService deviceInfrastructureService = Mockito.mock(DeviceInfrastructureService.class);

    @Test
    void testGetDeviceTreeRoot() {
        TopologyHandlerImpl topologyHandler = new TopologyHandlerImpl(deviceInfrastructureService);
        Mockito.when(deviceInfrastructureService.getTreeRoot()).thenReturn(null);

        var result = topologyHandler.getDeviceTreeRoot();

        assertNull(result);
        Mockito.verify(deviceInfrastructureService, Mockito.times(1)).getTreeRoot();
    }

    @Test
    void testGetDeviceTreeRootWithMacAddress() {
        TopologyHandlerImpl topologyHandler = new TopologyHandlerImpl(deviceInfrastructureService);
        String macAddress = "00:11:22:33:44:55";
        Mockito.when(deviceInfrastructureService.getTreeRootedAt(MacAddress.of(macAddress)))
                .thenReturn(null);

        var result = topologyHandler.getDeviceTreeRoot(macAddress);

        assertNull(result);
        Mockito.verify(deviceInfrastructureService, Mockito.times(1)).getTreeRootedAt(MacAddress.of(macAddress));
    }
}
