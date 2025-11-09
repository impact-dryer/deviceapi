package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import com.impactdryer.deviceapi.devicemanagment.domain.DeviceRegistration;
import com.impactdryer.deviceapi.devicemanagment.domain.MacAddress;
import jakarta.transaction.Transactional;
import java.util.List;
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
    public List<DeviceRegistration> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(DeviceInfrastructureServiceImpl::getDeviceRegistration)
                .toList();
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
