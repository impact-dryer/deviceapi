package com.impactdryer.deviceapi.devicemanagment.web;

import static org.junit.jupiter.api.Assertions.*;

import com.impactdryer.deviceapi.devicemanagment.application.DeviceDTO;
import com.impactdryer.deviceapi.devicemanagment.application.handlers.GetDevicesHandler;
import com.impactdryer.deviceapi.devicemanagment.application.handlers.RegisterDeviceHandler;
import com.impactdryer.deviceapi.infrastructure.openapi.DevicesApiDelegate;
import com.impactdryer.deviceapi.infrastructure.openapi.model.DeviceRegistrationRequest;
import com.impactdryer.deviceapi.infrastructure.openapi.model.DeviceSummary;
import com.impactdryer.deviceapi.infrastructure.openapi.model.DeviceType;
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
        assertEquals("/devices/1", deviceSummaryResponseEntity.getHeaders().getFirst("Location"));
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
}
