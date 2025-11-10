package com.impactdryer.deviceapi.devicemanagment.application.handlers;

import com.impactdryer.deviceapi.devicemanagment.application.DeviceDTO;
import com.impactdryer.deviceapi.devicemanagment.application.query.GetDeviceByMacQuery;
import java.util.List;

public interface GetDevicesHandler {
    DeviceDTO handle(GetDeviceByMacQuery command);

    List<DeviceDTO> getSortedDevices();
}
