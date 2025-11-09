package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import com.impactdryer.deviceapi.devicemanagment.domain.DeviceRegistration;
import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;
import jakarta.transaction.Transactional;

public interface DeviceInfrastructureService {
    @Transactional
    Long registerDevice(DeviceRegistration deviceRegistration);

    DeviceRegistration getDeviceByMac(MacAddress mac);
}
