package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.impactdryer.deviceapi.devicemanagment.domain.DeviceNode;
import com.impactdryer.deviceapi.devicemanagment.domain.DeviceRegistration;
import com.impactdryer.deviceapi.devicemanagment.domain.DeviceType;
import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class DeviceInfrastructureServiceTest {
    private DeviceRepository deviceRepository = Mockito.mock(DeviceRepository.class);

    private DeviceInfrastructureServiceImpl deviceInfrastructureService =
            new DeviceInfrastructureServiceImpl(deviceRepository);

    private static DeviceEntity device(String mac, DeviceType type, DeviceEntity uplink) {
        DeviceEntity e = new DeviceEntity();
        e.setId(System.nanoTime()); // unique enough for tests
        e.setMacAddress(mac);
        e.setDeviceType(type);
        if (uplink != null) {
            e.setUplink(uplink);
        }
        return e;
    }

    private static DeviceNode onlyChild(DeviceNode node) {
        assertEquals(1, node.getDownlinks().size(), "Expected exactly one child");
        return node.getDownlinks().iterator().next();
    }

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

    @Test
    void returnsNullWhenNoDevices() {
        when(deviceRepository.findAllRecursive()).thenReturn(List.of());

        Assertions.assertThrows(NoRootDeviceFoundException.class, () -> deviceInfrastructureService.getTreeRoot());
    }

    @Test
    void buildsSingleNodeTopology() {
        DeviceEntity rootEntity = device("AA:AA:AA:AA:AA:AA", DeviceType.GATEWAY, null);

        when(deviceRepository.findAllRecursive()).thenReturn(List.of(rootEntity));

        DeviceNode root = deviceInfrastructureService.getTreeRoot();

        assertNotNull(root);
        assertEquals("AA:AA:AA:AA:AA:AA", root.getMacAddress().value());
        assertEquals(DeviceType.GATEWAY, root.getDeviceType());
        assertNotNull(root.getDownlinks());
        assertTrue(root.getDownlinks().isEmpty());
    }

    @Test
    void buildsMultiLevelTopologyRegardlessOfInputOrder() {
        // Topology:
        // R
        // ├─ A
        // │  └─ A1
        // └─ B
        //    └─ B1
        DeviceEntity R = device("00:00:00:00:00:01", DeviceType.GATEWAY, null);
        DeviceEntity A = device("00:00:00:00:00:02", DeviceType.SWITCH, R);
        DeviceEntity B = device("00:00:00:00:00:03", DeviceType.SWITCH, R);
        DeviceEntity A1 = device("00:00:00:00:00:04", DeviceType.ACCESS_POINT, A);
        DeviceEntity B1 = device("00:00:00:00:00:05", DeviceType.ACCESS_POINT, B);

        // Unordered input list to ensure algorithm doesn't rely on order
        when(deviceRepository.findAllRecursive()).thenReturn(List.of(B1, A, R, A1, B));

        DeviceNode root = deviceInfrastructureService.getTreeRoot();

        assertNotNull(root);
        assertEquals("00:00:00:00:00:01", root.getMacAddress().value());
        // Root children should be A and B (order agnostic)
        Set<String> rootChildren = root.getDownlinks().stream()
                .map(n -> n.getMacAddress().value())
                .collect(java.util.stream.Collectors.toSet());
        assertEquals(Set.of("00:00:00:00:00:02", "00:00:00:00:00:03"), rootChildren);

        // Verify A subtree
        DeviceNode nodeA = root.getDownlinks().stream()
                .filter(n -> n.getMacAddress().value().equals("00:00:00:00:00:02"))
                .findFirst()
                .orElseThrow();
        assertEquals(1, nodeA.getDownlinks().size());
        assertEquals(
                "00:00:00:00:00:04",
                nodeA.getDownlinks().stream().toList().get(0).getMacAddress().value());

        // Verify B subtree
        DeviceNode nodeB = root.getDownlinks().stream()
                .filter(n -> n.getMacAddress().value().equals("00:00:00:00:00:03"))
                .findFirst()
                .orElseThrow();
        assertEquals(1, nodeB.getDownlinks().size());
        assertEquals(
                "00:00:00:00:00:05",
                nodeB.getDownlinks().stream().toList().get(0).getMacAddress().value());
    }

    @Test
    void buildsWideTopologyThreeChildren() {
        DeviceEntity R = device("00:00:00:00:10:00", DeviceType.GATEWAY, null);
        DeviceEntity C1 = device("00:00:00:00:10:01", DeviceType.SWITCH, R);
        DeviceEntity C2 = device("00:00:00:00:10:02", DeviceType.SWITCH, R);
        DeviceEntity C3 = device("00:00:00:00:10:03", DeviceType.ACCESS_POINT, R);

        when(deviceRepository.findAllRecursive()).thenReturn(List.of(C2, C1, C3, R));

        DeviceNode root = deviceInfrastructureService.getTreeRoot();

        assertNotNull(root);
        assertEquals("00:00:00:00:10:00", root.getMacAddress().value());
        Set<String> childMacs =
                root.getDownlinks().stream().map(n -> n.getMacAddress().value()).collect(Collectors.toSet());
        assertEquals(Set.of("00:00:00:00:10:01", "00:00:00:00:10:02", "00:00:00:00:10:03"), childMacs);
    }

    @Test
    void buildsDeepChainTopologyFiveLevels() {
        DeviceEntity R = device("00:00:00:00:20:00", DeviceType.GATEWAY, null);
        DeviceEntity A = device("00:00:00:00:20:01", DeviceType.SWITCH, R);
        DeviceEntity B = device("00:00:00:00:20:02", DeviceType.SWITCH, A);
        DeviceEntity C = device("00:00:00:00:20:03", DeviceType.SWITCH, B);
        DeviceEntity D = device("00:00:00:00:20:04", DeviceType.ACCESS_POINT, C);

        when(deviceRepository.findAllRecursive()).thenReturn(List.of(D, C, A, B, R));

        DeviceNode root = deviceInfrastructureService.getTreeRoot();

        assertNotNull(root);
        assertEquals("00:00:00:00:20:00", root.getMacAddress().value());
        DeviceNode nA = onlyChild(root);
        assertEquals("00:00:00:00:20:01", nA.getMacAddress().value());
        DeviceNode nB = onlyChild(nA);
        assertEquals("00:00:00:00:20:02", nB.getMacAddress().value());
        DeviceNode nC = onlyChild(nB);
        assertEquals("00:00:00:00:20:03", nC.getMacAddress().value());
        DeviceNode nD = onlyChild(nC);
        assertEquals("00:00:00:00:20:04", nD.getMacAddress().value());
        assertTrue(nD.getDownlinks().isEmpty());
    }

    @Test
    void throwsWhenNoRootDueToCycle() {
        // A <-> B cycle (no node with null uplink)
        DeviceEntity A = device("00:00:00:00:30:01", DeviceType.SWITCH, null);
        DeviceEntity B = device("00:00:00:00:30:02", DeviceType.SWITCH, null);
        // create the cycle
        A.setUplink(B);
        B.setUplink(A);

        when(deviceRepository.findAllRecursive()).thenReturn(List.of(A, B));

        Assertions.assertThrows(NoRootDeviceFoundException.class, () -> deviceInfrastructureService.getTreeRoot());
    }

    @Test
    void shouldThrowOnMultipleRootsWhenNotAllowed() {
        DeviceEntity gateway1 =
                new DeviceEntity(TestingUtils.getRandomMacAddress().value(), DeviceType.GATEWAY);
        DeviceEntity gateway2 =
                new DeviceEntity(TestingUtils.getRandomMacAddress().value(), DeviceType.GATEWAY);
        Mockito.when(deviceRepository.findAllRecursive()).thenReturn(List.of(gateway1, gateway2));

        assertThrows(MultipleRootDevicesFoundException.class, () -> deviceInfrastructureService.getTreeRoot());
    }
}
