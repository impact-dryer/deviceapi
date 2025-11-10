package com.impactdryer.deviceapi.devicemanagment.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.impactdryer.deviceapi.devicemanagment.infrastructure.TestingUtils;
import org.junit.jupiter.api.Test;

class DeviceNodeTest {

    @Test
    void testDeviceNodeCreation() {
        MacAddress randomMacAddress = TestingUtils.getRandomMacAddress();
        DeviceNode node = new DeviceNode(randomMacAddress, DeviceType.SWITCH);
        assertNotNull(node);
        assertEquals(DeviceType.SWITCH, node.getDeviceType());
        assertEquals(randomMacAddress, node.getMacAddress());
    }

    @Test
    void testAddDownLink() {
        MacAddress parentMac = TestingUtils.getRandomMacAddress();
        DeviceNode parentNode = new DeviceNode(parentMac, DeviceType.GATEWAY);

        MacAddress childMac = TestingUtils.getRandomMacAddress();
        DeviceNode childNode = new DeviceNode(childMac, DeviceType.SWITCH);

        parentNode.addDownlink(childNode);

        assertTrue(parentNode.getDownlinks().contains(childNode));
        assertEquals(parentNode, childNode.getUplink());
    }

    @Test
    void testAddUpLink() {
        MacAddress parentMac = TestingUtils.getRandomMacAddress();
        DeviceNode parentNode = new DeviceNode(parentMac, DeviceType.GATEWAY);

        MacAddress childMac = TestingUtils.getRandomMacAddress();
        DeviceNode childNode = new DeviceNode(childMac, DeviceType.SWITCH);

        childNode.setUplink(parentNode);

        assertEquals(parentNode, childNode.getUplink());
        assertTrue(parentNode.getDownlinks().contains(childNode));
    }

    @Test
    void testEqualsAndHashCode() {
        MacAddress mac1 = TestingUtils.getRandomMacAddress();
        MacAddress mac2 = TestingUtils.getRandomMacAddress();

        DeviceNode node1a = new DeviceNode(mac1, DeviceType.GATEWAY);
        DeviceNode node1b = new DeviceNode(mac1, DeviceType.GATEWAY);
        DeviceNode node2 = new DeviceNode(mac2, DeviceType.GATEWAY);

        assertEquals(node1a, node1b);
        assertNotEquals(node1a, node2);
        assertEquals(node1a.hashCode(), node1b.hashCode());
        assertNotEquals(node1a.hashCode(), node2.hashCode());
    }

    @Test
    void testEquals() {
        MacAddress mac1 = TestingUtils.getRandomMacAddress();
        MacAddress mac2 = TestingUtils.getRandomMacAddress();

        DeviceNode node1 = new DeviceNode(mac1, DeviceType.GATEWAY);
        DeviceNode node2 = new DeviceNode(mac1, DeviceType.GATEWAY);
        DeviceNode node3 = new DeviceNode(mac2, DeviceType.SWITCH);

        assertEquals(node1, node2);
        assertNotEquals(node1, node3);
    }

    @Test
    void testHashCode() {
        MacAddress mac1 = TestingUtils.getRandomMacAddress();

        DeviceNode node1 = new DeviceNode(mac1, DeviceType.GATEWAY);
        DeviceNode node2 = new DeviceNode(mac1, DeviceType.GATEWAY);

        assertEquals(node1.hashCode(), node2.hashCode());
    }
}
