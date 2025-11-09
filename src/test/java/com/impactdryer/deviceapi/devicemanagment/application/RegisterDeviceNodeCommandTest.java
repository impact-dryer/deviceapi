package com.impactdryer.deviceapi.devicemanagment.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RegisterDeviceNodeCommandTest {

    @Test
    void testRegisterDeviceCommandCreation() {
        RegisterDeviceCommand command = new RegisterDeviceCommand("type1", "address", "uplink");
        assertEquals("type1", command.deviceType());
        assertEquals("address", command.macAddress());
        assertEquals("uplink", command.uplinkMacAddress());
    }

    @Test
    void testRegisterDeviceCommandWithEmptyDeviceType() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RegisterDeviceCommand("", "address", "uplink");
        });
    }

    @Test
    void testRegisterDeviceCommandWithNullUplinkMacAddress() {
        assertDoesNotThrow(() -> {
            new RegisterDeviceCommand("type1", "address", null);
        });
    }

    @Test
    void testRegisterDeviceCommandWithEmptyDeviceAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            new RegisterDeviceCommand("type1", "", "uplink");
        });
    }
}
