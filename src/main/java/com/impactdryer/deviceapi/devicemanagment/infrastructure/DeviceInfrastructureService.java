package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import com.impactdryer.deviceapi.devicemanagment.domain.DeviceRegistration;
import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;
import jakarta.transaction.Transactional;
import java.util.List;

public interface DeviceInfrastructureService {
    @Transactional
    Long registerDevice(DeviceRegistration deviceRegistration);

    DeviceRegistration getDeviceByMac(MacAddress mac);

    List<DeviceRegistration> getAllDevices();

    // Retrieve devices sorted by database precedence (Gateway > Switch > Access Point)
    List<DeviceRegistration> getAllDevicesSortedByType();
}
