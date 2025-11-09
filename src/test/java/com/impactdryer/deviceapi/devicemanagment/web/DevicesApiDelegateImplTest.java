package com.impactdryer.deviceapi.devicemanagment.web;

import static org.junit.jupiter.api.Assertions.*;

import com.impactdryer.deviceapi.devicemanagment.application.DeviceDTO;
import com.impactdryer.deviceapi.devicemanagment.application.handlers.GetDevicesHandler;
import com.impactdryer.deviceapi.devicemanagment.application.handlers.RegisterDeviceHandler;
import com.impactdryer.deviceapi.infrastructure.openapi.DevicesApiDelegate;
import com.impactdryer.deviceapi.infrastructure.openapi.model.DeviceRegistrationRequest;
import com.impactdryer.deviceapi.infrastructure.openapi.model.DeviceSummary;
import com.impactdryer.deviceapi.infrastructure.openapi.model.DeviceType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

class DevicesApiDelegateImplTest {
    RegisterDeviceHandler registerDeviceHandler = Mockito.mock(RegisterDeviceHandler.class);
    GetDevicesHandler getDevicesHandler = Mockito.mock(GetDevicesHandler.class);
    DevicesApiDelegate devicesApiDelegate = new DevicesApiDelegateImpl(registerDeviceHandler, getDevicesHandler);

    @Test
    public void shouldAddLocationHeaderWhenCreatingDevice() {
        DeviceRegistrationRequest deviceRegistrationRequest = new DeviceRegistrationRequest();
        deviceRegistrationRequest.deviceType(DeviceType.SWITCH);
        deviceRegistrationRequest.macAddress("7b:12:d6:29:0b:ee");
        Mockito.when(registerDeviceHandler.registerDevice(Mockito.any())).thenReturn(1L);
        ResponseEntity<DeviceSummary> deviceSummaryResponseEntity =
                devicesApiDelegate.registerDevice(deviceRegistrationRequest);
        assertEquals(201, deviceSummaryResponseEntity.getStatusCodeValue());
        assertTrue(deviceSummaryResponseEntity.getHeaders().containsKey("Location"));
        assertEquals(
                "/devices/7b:12:d6:29:0b:ee",
                deviceSummaryResponseEntity.getHeaders().getFirst("Location"));
    }

    @Test
    public void shouldReturnDeviceWhenFound() {
        String macAddress = "7b:12:d6:29:0b:ee";
        Mockito.when(getDevicesHandler.handle(Mockito.any()))
                .thenReturn(new DeviceDTO(macAddress, DeviceType.SWITCH.getValue()));
        ResponseEntity<DeviceSummary> responseEntity = devicesApiDelegate.getDeviceByMac(macAddress);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(macAddress, responseEntity.getBody().getMacAddress());
        assertEquals(DeviceType.SWITCH, responseEntity.getBody().getDeviceType());
    }

    @Test
    public void shouldListDevicesSortedAndMapped() {
        Mockito.when(getDevicesHandler.getSortedDevices())
                .thenReturn(List.of(
                        new DeviceDTO("AA:BB:CC:DD:EE:01", "GATEWAY"),
                        new DeviceDTO("AA:BB:CC:DD:EE:02", "SWITCH"),
                        new DeviceDTO("AA:BB:CC:DD:EE:03", "ACCESS_POINT")));

        ResponseEntity<List<DeviceSummary>> response = devicesApiDelegate.listDevices();
        assertEquals(200, response.getStatusCodeValue());
        List<DeviceSummary> body = response.getBody();
        assertNotNull(body);
        assertEquals(3, body.size());
        assertEquals(DeviceType.GATEWAY, body.get(0).getDeviceType());
        assertEquals("AA:BB:CC:DD:EE:01", body.get(0).getMacAddress());
        assertEquals(DeviceType.SWITCH, body.get(1).getDeviceType());
        assertEquals("AA:BB:CC:DD:EE:02", body.get(1).getMacAddress());
        assertEquals(DeviceType.ACCESS_POINT, body.get(2).getDeviceType());
        assertEquals("AA:BB:CC:DD:EE:03", body.get(2).getMacAddress());
    }
}
