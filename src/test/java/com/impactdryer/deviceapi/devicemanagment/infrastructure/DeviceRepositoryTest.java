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

    private static @NotNull DeviceEntity getDeviceEntity(DeviceType accessPoint, DeviceEntity switch1) {
        DeviceEntity ap1 = new DeviceEntity();
        ap1.setMacAddress(TestingUtils.getRandomMacAddress().value());
        ap1.setDeviceType(accessPoint);
        ap1.setUplink(switch1);
        return ap1;
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

    @Test
    void testFindAllRecursive() {
        DeviceEntity gateway = new DeviceEntity();
        gateway.setMacAddress(TestingUtils.getRandomMacAddress().value());
        gateway.setDeviceType(DeviceType.GATEWAY);
        deviceRepository.save(gateway);

        DeviceEntity switch1 = getDeviceEntity(DeviceType.SWITCH, gateway);
        deviceRepository.save(switch1);

        DeviceEntity ap1 = getDeviceEntity(DeviceType.ACCESS_POINT, switch1);
        deviceRepository.save(ap1);

        DeviceEntity switch2 = getDeviceEntity(DeviceType.SWITCH, gateway);
        deviceRepository.save(switch2);

        List<DeviceEntity> allRecursive = deviceRepository.findAllRecursive();
        assertEquals(4, allRecursive.size());
        assertEquals(gateway.getMacAddress(), allRecursive.get(0).getMacAddress());
        assertEquals(switch1.getMacAddress(), allRecursive.get(1).getMacAddress());
        assertEquals(ap1.getMacAddress(), allRecursive.get(2).getMacAddress());
        assertEquals(switch2.getMacAddress(), allRecursive.get(3).getMacAddress());
    }

    @Test
    void testFindAllRecursiveFromMacAddress() {
        DeviceEntity gateway = new DeviceEntity();
        gateway.setMacAddress(TestingUtils.getRandomMacAddress().value());
        gateway.setDeviceType(DeviceType.GATEWAY);
        deviceRepository.save(gateway);

        DeviceEntity switch1 = getDeviceEntity(DeviceType.SWITCH, gateway);
        deviceRepository.save(switch1);

        DeviceEntity ap1 = getDeviceEntity(DeviceType.ACCESS_POINT, switch1);
        deviceRepository.save(ap1);

        DeviceEntity switch2 = getDeviceEntity(DeviceType.SWITCH, gateway);
        deviceRepository.save(switch2);

        List<DeviceEntity> allRecursive = deviceRepository.findAllFromDevice(switch1.getMacAddress());
        assertEquals(2, allRecursive.size());
        assertEquals(switch1.getMacAddress(), allRecursive.get(0).getMacAddress());
        assertEquals(ap1.getMacAddress(), allRecursive.get(1).getMacAddress());
    }
}
