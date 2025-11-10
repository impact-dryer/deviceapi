package com.impactdryer.deviceapi.devicemanagment.web;

import static org.junit.jupiter.api.Assertions.*;

import com.impactdryer.deviceapi.devicemanagment.application.handlers.TopologyHandler;
import com.impactdryer.deviceapi.devicemanagment.domain.DeviceNode;
import com.impactdryer.deviceapi.devicemanagment.domain.DeviceType;
import com.impactdryer.deviceapi.devicemanagment.infrastructure.TestingUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TopologyApiDelegateImplTest {
    TopologyHandler topologyHandler = Mockito.mock(TopologyHandler.class);
    TopologyApiDelegateImpl delegate = new TopologyApiDelegateImpl(topologyHandler);

    private static @NotNull DeviceNode getDeviceNode() {
        return new DeviceNode(TestingUtils.getRandomMacAddress(), DeviceType.GATEWAY);
    }

    @Test
    void testMappingOfDeviceNode() {
        DeviceNode root = getDeviceNode();
        DeviceNode downlink = getDeviceNode();
        DeviceNode leaf = getDeviceNode();
        downlink.addDownlink(leaf);
        root.addDownlink(downlink);
        Mockito.when(topologyHandler.getDeviceTreeRoot()).thenReturn(root);

        var response = delegate.getFullTopology();
        assertEquals(200, response.getStatusCodeValue());
        var body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        var topologyRoot = body.get(0);
        assertEquals(root.getMacAddress().value(), topologyRoot.getMacAddress());
        assertEquals(1, topologyRoot.getChildren().size());
        var topologyDownlink = topologyRoot.getChildren().get(0);
        assertEquals(downlink.getMacAddress().value(), topologyDownlink.getMacAddress());
        assertEquals(1, topologyDownlink.getChildren().size());
        var topologyLeaf = topologyDownlink.getChildren().get(0);
        assertEquals(leaf.getMacAddress().value(), topologyLeaf.getMacAddress());
        assertEquals(0, topologyLeaf.getChildren().size());
    }

    @Test
    void testGetSubtreeFromDevice() {
        DeviceNode root = getDeviceNode();
        DeviceNode downlink = getDeviceNode();
        DeviceNode leaf = getDeviceNode();
        downlink.addDownlink(leaf);
        root.addDownlink(downlink);
        Mockito.when(topologyHandler.getDeviceTreeRoot(downlink.getMacAddress().value()))
                .thenReturn(downlink);

        var response = delegate.getSubtreeFromDevice(downlink.getMacAddress().value());
        assertEquals(200, response.getStatusCodeValue());
        var topologyRoot = response.getBody();
        assertNotNull(topologyRoot);
        assertEquals(downlink.getMacAddress().value(), topologyRoot.getMacAddress());
        assertEquals(1, topologyRoot.getChildren().size());
        var topologyLeaf = topologyRoot.getChildren().get(0);
        assertEquals(leaf.getMacAddress().value(), topologyLeaf.getMacAddress());
        assertEquals(0, topologyLeaf.getChildren().size());
    }
}
