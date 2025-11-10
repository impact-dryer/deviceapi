package com.impactdryer.deviceapi.devicemanagment.application.handlers;

import static org.junit.jupiter.api.Assertions.*;

import com.impactdryer.deviceapi.devicemanagment.application.DeviceDTO;
import com.impactdryer.deviceapi.devicemanagment.application.commands.GetDeviceByMacCommand;
import com.impactdryer.deviceapi.devicemanagment.application.handlers.impl.GetDevicesHandlerImpl;
import com.impactdryer.deviceapi.devicemanagment.domain.DeviceRegistration;
import com.impactdryer.deviceapi.devicemanagment.domain.DeviceType;
import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;
import com.impactdryer.deviceapi.devicemanagment.infrastructure.DeviceInfrastructureService;
import com.impactdryer.deviceapi.devicemanagment.infrastructure.TestingUtils;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GetDevicesHandlerImplTest {

    private final DeviceInfrastructureService infrastructureService = Mockito.mock(DeviceInfrastructureService.class);
    private final GetDevicesHandler handler = new GetDevicesHandlerImpl(infrastructureService);

    @Test
    void shouldReturnDevices() {
        // Given unsorted devices: ACCESS_POINT, SWITCH, GATEWAY
        MacAddress macAp = TestingUtils.getRandomMacAddress();
        MacAddress macSw = TestingUtils.getRandomMacAddress();
        MacAddress macGw = TestingUtils.getRandomMacAddress();
        List<DeviceRegistration> unsorted = List.of(
                new DeviceRegistration(macAp, DeviceType.ACCESS_POINT),
                new DeviceRegistration(macSw, DeviceType.SWITCH),
                new DeviceRegistration(macGw, DeviceType.GATEWAY));
        Mockito.when(infrastructureService.getAllDevicesSortedByType()).thenReturn(unsorted);

        // When
        List<DeviceDTO> result = handler.getSortedDevices();

        assertEquals(3, result.size());
    }

    @Test
    void shouldReturnDeviceByMac() {
        // Given
        MacAddress mac = TestingUtils.getRandomMacAddress();
        DeviceRegistration registration = new DeviceRegistration(mac, DeviceType.SWITCH);
        Mockito.when(infrastructureService.getDeviceByMac(mac)).thenReturn(registration);

        // When
        DeviceDTO result = handler.handle(new GetDeviceByMacCommand(mac.value()));

        // Then
        assertEquals(mac.value(), result.getMacAddress());
        assertEquals(DeviceType.SWITCH.name(), result.getDeviceType());
    }
}
