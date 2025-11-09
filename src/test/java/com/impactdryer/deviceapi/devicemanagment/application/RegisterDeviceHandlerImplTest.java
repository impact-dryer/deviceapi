package com.impactdryer.deviceapi.devicemanagment.application;

import static org.junit.jupiter.api.Assertions.*;

import com.impactdryer.deviceapi.devicemanagment.application.commands.RegisterDeviceCommand;
import com.impactdryer.deviceapi.devicemanagment.application.handlers.RegisterDeviceHandler;
import com.impactdryer.deviceapi.devicemanagment.application.handlers.impl.RegisterDeviceHandlerImpl;
import com.impactdryer.deviceapi.devicemanagment.infrastructure.DeviceInfrastructureService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RegisterDeviceHandlerImplTest {

    private DeviceInfrastructureService deviceInfrastructureService = Mockito.mock(DeviceInfrastructureService.class);
    private RegisterDeviceHandler registerDeviceHandler = new RegisterDeviceHandlerImpl(deviceInfrastructureService);

    @Test
    void testRegisterDevice() {
        RegisterDeviceCommand command = new RegisterDeviceCommand("SWITCH", "7b:12:d6:29:0b:ee", null);
        Mockito.when(deviceInfrastructureService.registerDevice(Mockito.any())).thenReturn(1L);

        Long deviceId = registerDeviceHandler.registerDevice(command);

        assertEquals(1L, deviceId);
        Mockito.verify(deviceInfrastructureService, Mockito.times(1)).registerDevice(Mockito.any());
    }

    @Test
    void testRegisterDeviceWithUplink() {
        RegisterDeviceCommand command = new RegisterDeviceCommand("SWITCH", "7b:12:d6:29:0b:ee", "0d:69:d7:cc:f6:d9");
        Mockito.when(deviceInfrastructureService.registerDevice(Mockito.any())).thenReturn(1L);

        Long deviceId = registerDeviceHandler.registerDevice(command);

        assertEquals(1L, deviceId);
        Mockito.verify(deviceInfrastructureService, Mockito.times(1)).registerDevice(Mockito.any());
    }
}
