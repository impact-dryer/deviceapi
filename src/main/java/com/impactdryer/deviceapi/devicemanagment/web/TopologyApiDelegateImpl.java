package com.impactdryer.deviceapi.devicemanagment.web;

import com.impactdryer.deviceapi.devicemanagment.application.handlers.TopologyHandler;
import com.impactdryer.deviceapi.devicemanagment.domain.DeviceNode;
import com.impactdryer.deviceapi.infrastructure.openapi.TopologyApiDelegate;
import com.impactdryer.deviceapi.infrastructure.openapi.model.TopologyNode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class TopologyApiDelegateImpl implements TopologyApiDelegate {
    private final TopologyHandler topologyHandler;

    private static TopologyNode mapToTopologyNode(DeviceNode deviceNode) {
        TopologyNode topologyNode = new TopologyNode();
        topologyNode.setMacAddress(deviceNode.getMacAddress().value());
        List<TopologyNode> children = deviceNode.getDownlinks().stream()
                .map(TopologyApiDelegateImpl::mapToTopologyNode)
                .toList();
        topologyNode.setChildren(children);
        return topologyNode;
    }

    @Override
    public ResponseEntity<List<TopologyNode>> getFullTopology() {
        DeviceNode deviceTreeRoot = topologyHandler.getDeviceTreeRoot();
        TopologyNode topologyRoot = mapToTopologyNode(deviceTreeRoot);
        return ResponseEntity.ok(List.of(topologyRoot));
    }

    @Override
    public ResponseEntity<TopologyNode> getSubtreeFromDevice(String macAddress) {
        DeviceNode deviceTreeRoot = topologyHandler.getDeviceTreeRoot(macAddress);
        TopologyNode topologyRoot = mapToTopologyNode(deviceTreeRoot);
        return ResponseEntity.ok(topologyRoot);
    }
}
