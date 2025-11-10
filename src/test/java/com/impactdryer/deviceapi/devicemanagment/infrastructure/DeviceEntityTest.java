package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DeviceEntityTest {

    @Test
    void testEquals() {
        DeviceEntity device1 = new DeviceEntity();
        device1.setId(1L);
        device1.setMacAddress("00:11:22:33:44:55");

        DeviceEntity device2 = new DeviceEntity();
        device2.setId(1L);
        device2.setMacAddress("00:11:22:33:44:55");

        DeviceEntity device3 = new DeviceEntity();
        device3.setId(2L);
        device3.setMacAddress("66:77:88:99:AA:BB");

        assertEquals(device1, device2);
        assertNotEquals(device1, device3);
    }

    @Test
    void testHashCode() {
        DeviceEntity device1 = new DeviceEntity();
        device1.setId(1L);
        device1.setMacAddress("00:11:22:33:44:55");

        DeviceEntity device2 = new DeviceEntity();
        device2.setId(1L);
        device2.setMacAddress("00:11:22:33:44:55");

        assertEquals(device1.hashCode(), device2.hashCode());
    }
}
