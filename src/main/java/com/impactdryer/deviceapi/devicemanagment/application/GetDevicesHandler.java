package com.impactdryer.deviceapi.devicemanagment.application;

public interface GetDevicesHandler {
    DeviceDTO handle(GetDeviceByMacCommand command);
}
