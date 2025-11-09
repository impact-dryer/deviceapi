package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import com.impactdryer.deviceapi.devicemanagment.domain.DeviceType;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
class DeviceRepositoryTest extends AbstractPostgresContainerTest {
    @Autowired
    private DeviceRepository deviceRepository;

    private static @NotNull DeviceEntity getDeviceEntity() {
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setDeviceType(DeviceType.SWITCH);
        deviceEntity.setMacAddress(TestingUtils.getRandomMacAddress().value());
        return deviceEntity;
    }

    @BeforeEach
    void setup() {
        deviceRepository.deleteAll();
    }

    @Test
    void testSaveAndFindDeviceEntity() {
        DeviceEntity deviceEntity = getDeviceEntity();
        deviceEntity.setDownlinks(Set.of(getDeviceEntity()));
        DeviceEntity savedEntity = deviceRepository.save(deviceEntity);
        assertNotNull(savedEntity.getId());
        DeviceEntity foundEntity =
                deviceRepository.findById(savedEntity.getId()).orElseThrow();
        assertEquals(savedEntity.getMacAddress(), foundEntity.getMacAddress());
        assertEquals(1, foundEntity.getDownlinks().size());
    }

    @Test
    void shouldReturnDatabaseSortedOrder() {
        deviceRepository.save(
                new DeviceEntity(TestingUtils.getRandomMacAddress().value(), DeviceType.ACCESS_POINT));
        deviceRepository.save(
                new DeviceEntity(TestingUtils.getRandomMacAddress().value(), DeviceType.SWITCH));
        deviceRepository.save(
                new DeviceEntity(TestingUtils.getRandomMacAddress().value(), DeviceType.GATEWAY));

        List<DeviceEntity> sorted = deviceRepository.findAllSortedByType();
        assertEquals(3, sorted.size());
        assertEquals(DeviceType.GATEWAY, sorted.get(0).getDeviceType());
        assertEquals(DeviceType.SWITCH, sorted.get(1).getDeviceType());
        assertEquals(DeviceType.ACCESS_POINT, sorted.get(2).getDeviceType());
    }
}
