package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import com.impactdryer.deviceapi.devicemanagment.domain.DeviceNode;
import com.impactdryer.deviceapi.devicemanagment.domain.DeviceRegistration;
import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceInfrastructureServiceImpl implements DeviceInfrastructureService {
    private final DeviceRepository deviceRepository;

    private static DeviceRegistration getDeviceRegistration(DeviceEntity deviceEntity) {
        return new DeviceRegistration(
                MacAddress.of(deviceEntity.getMacAddress()),
                deviceEntity.getDeviceType(),
                deviceEntity.getUplink() != null
                        ? MacAddress.of(deviceEntity.getUplink().getMacAddress())
                        : null);
    }

    private static void setUpLink(Map<String, DeviceNode> nodeMap, DeviceEntity deviceEntity) {
        DeviceNode parentNode = nodeMap.get(deviceEntity.getUplink().getMacAddress());
        DeviceNode currentNode = nodeMap.get(deviceEntity.getMacAddress());
        parentNode.addDownlink(currentNode);
    }

    @Transactional
    @Override
    public Long registerDevice(DeviceRegistration deviceRegistration) {
        DeviceEntity deviceEntity = saveDevice(deviceRegistration);
        return deviceEntity.getId();
    }

    @Override
    public DeviceRegistration getDeviceByMac(MacAddress mac) {
        return deviceRepository
                .findByMacAddress(mac.value())
                .map(DeviceInfrastructureServiceImpl::getDeviceRegistration)
                .orElseThrow(() -> new DeviceNotFound(mac));
    }

    @Override
    public DeviceNode getTreeRoot() {
        List<DeviceEntity> allRecursive = this.deviceRepository.findAllRecursive();
        Map<String, DeviceNode> nodeMap = allRecursive.stream()
                .collect(Collectors.toMap(
                        DeviceEntity::getMacAddress,
                        v -> new DeviceNode(MacAddress.of(v.getMacAddress()), v.getDeviceType())));
        DeviceNode root = null;
        for (int i = 0; i < allRecursive.size(); i++) {
            DeviceEntity deviceEntity = allRecursive.get(i);
            if (deviceEntity.getUplink() != null) {
                setUpLink(nodeMap, deviceEntity);
            } else {
                if (root != null) {
                    throw new MultipleRootDevicesFoundException();
                }
                root = nodeMap.get(deviceEntity.getMacAddress());
            }
        }
        if (root == null) {
            throw new NoRootDeviceFoundException();
        }
        return root;
    }

    @Override
    public List<DeviceRegistration> getAllDevicesSortedByType() {
        return deviceRepository.findAllSortedByType().stream()
                .map(DeviceInfrastructureServiceImpl::getDeviceRegistration)
                .toList();
    }

    private DeviceEntity saveDevice(DeviceRegistration deviceRegistration) {
        DeviceEntity deviceEntity = new DeviceEntity();
        deviceEntity.setDeviceType(deviceRegistration.getDeviceType());
        deviceEntity.setMacAddress(deviceRegistration.getDeviceMacAddress().value());
        if (deviceRegistration.getUplinkMacAddress() != null) {
            DeviceEntity uplinkEntity = deviceRepository
                    .findByMacAddress(deviceRegistration.getUplinkMacAddress().value())
                    .orElseThrow(() -> new NoUplinkFoundException(deviceRegistration.getUplinkMacAddress()));
            deviceEntity.setUplink(uplinkEntity);
        }
        return deviceRepository.save(deviceEntity);
    }
}
