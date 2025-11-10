package com.impactdryer.deviceapi.devicemanagment.application.handlers;

import com.impactdryer.deviceapi.devicemanagment.domain.DeviceNode;

public interface TopologyHandler {
    DeviceNode getDeviceTreeRoot();

    DeviceNode getDeviceTreeRoot(String macAddress);
}
