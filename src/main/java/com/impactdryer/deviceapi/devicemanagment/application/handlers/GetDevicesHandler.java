package com.impactdryer.deviceapi.devicemanagment.application.handlers;

import com.impactdryer.deviceapi.devicemanagment.application.DeviceDTO;
import com.impactdryer.deviceapi.devicemanagment.application.commands.GetDeviceByMacCommand;
import java.util.List;

public interface GetDevicesHandler {
    DeviceDTO handle(GetDeviceByMacCommand command);

    List<DeviceDTO> getSortedDevices();
}
