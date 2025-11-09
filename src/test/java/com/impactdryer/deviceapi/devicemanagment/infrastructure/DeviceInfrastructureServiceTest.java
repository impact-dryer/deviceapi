package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import com.impactdryer.deviceapi.devicemanagment.domain.DeviceRegistration;
import com.impactdryer.deviceapi.devicemanagment.domain.DeviceType;
import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class DeviceInfrastructureServiceTest {
    private DeviceRepository deviceRepository = Mockito.mock(DeviceRepository.class);

    private DeviceInfrastructureServiceImpl deviceInfrastructureService =
            new DeviceInfrastructureServiceImpl(deviceRepository);

    @Test
    void testSaveDeviceWithoutUpLink() {
        ArgumentCaptor<DeviceEntity> argumentCaptor = ArgumentCaptor.forClass(DeviceEntity.class);
        DeviceRegistration deviceRegistration =
                new DeviceRegistration(TestingUtils.getRandomMacAddress(), DeviceType.SWITCH);
        DeviceEntity saved = new DeviceEntity();
        saved.setId(1l);
        Mockito.when(deviceRepository.save(Mockito.any())).thenReturn(saved);
        deviceInfrastructureService.registerDevice(deviceRegistration);

        Mockito.verify(deviceRepository, Mockito.times(1)).save(argumentCaptor.capture());
        DeviceEntity savedEntity = argumentCaptor.getValue();
        assertEquals(deviceRegistration.getDeviceMacAddress().value(), savedEntity.getMacAddress());
        assertEquals(deviceRegistration.getDeviceType(), savedEntity.getDeviceType());
        assertNull(savedEntity.getUplink());
    }

    @Test
    void testSaveDeviceWithUpLink() {
        ArgumentCaptor<DeviceEntity> argumentCaptor = ArgumentCaptor.forClass(DeviceEntity.class);
        MacAddress uplinkMac = TestingUtils.getRandomMacAddress();
        DeviceRegistration deviceRegistration =
                new DeviceRegistration(TestingUtils.getRandomMacAddress(), DeviceType.GATEWAY, uplinkMac);
        DeviceEntity uplinkEntity = new DeviceEntity();
        uplinkEntity.setMacAddress(uplinkMac.value());
        uplinkEntity.setId(2l);
        DeviceEntity saved = new DeviceEntity();
        saved.setId(1l);
        Mockito.when(deviceRepository.save(Mockito.any())).thenReturn(saved);
        Mockito.when(deviceRepository.findByMacAddress(uplinkMac.value())).thenReturn(Optional.of(uplinkEntity));
        deviceInfrastructureService.registerDevice(deviceRegistration);

        Mockito.verify(deviceRepository, Mockito.times(1)).save(argumentCaptor.capture());
        DeviceEntity savedEntity = argumentCaptor.getValue();
        assertEquals(deviceRegistration.getDeviceMacAddress().value(), savedEntity.getMacAddress());
        assertEquals(deviceRegistration.getDeviceType(), savedEntity.getDeviceType());
        assertEquals(
                deviceRegistration.getUplinkMacAddress().value(),
                savedEntity.getUplink().getMacAddress());
    }

    @Test
    void testSaveDeviceWithNonExistingUpLink() {
        MacAddress uplinkMac = TestingUtils.getRandomMacAddress();
        DeviceRegistration deviceRegistration =
                new DeviceRegistration(TestingUtils.getRandomMacAddress(), DeviceType.GATEWAY, uplinkMac);
        Mockito.when(deviceRepository.findByMacAddress(uplinkMac.value())).thenReturn(Optional.empty());

        assertThrows(NoUplinkFoundException.class, () -> {
            deviceInfrastructureService.registerDevice(deviceRegistration);
        });
    }

    @Test
    void testGetDeviceByMac() {
        MacAddress deviceMac = TestingUtils.getRandomMacAddress();
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setMacAddress(deviceMac.value());
        deviceEntity.setDeviceType(DeviceType.SWITCH);
        Mockito.when(deviceRepository.findByMacAddress(deviceMac.value())).thenReturn(Optional.of(deviceEntity));

        DeviceRegistration deviceRegistration = deviceInfrastructureService.getDeviceByMac(deviceMac);

        assertEquals(deviceMac, deviceRegistration.getDeviceMacAddress());
        assertEquals(DeviceType.SWITCH, deviceRegistration.getDeviceType());
        assertNull(deviceRegistration.getUplinkMacAddress());
    }
}
