package com.impactdryer.deviceapi.devicemanagment.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;

@Getter
public final class DeviceNode {
    private final MacAddress macAddress;
    private final DeviceType deviceType;
    private final Set<DeviceNode> downlinks;

    @JsonIgnore
    private DeviceNode uplink;

    @JsonCreator
    public DeviceNode(MacAddress macAddress, DeviceType deviceType) {
        this.macAddress = macAddress;
        this.deviceType = deviceType;
        this.uplink = null;
        this.downlinks = new HashSet<>();
    }

    public void addDownlink(DeviceNode downlink) {
        this.downlinks.add(downlink);
        downlink.uplink = this;
    }

    public void setUplink(DeviceNode uplink) {
        this.uplink = uplink;
        uplink.addDownlink(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DeviceNode that = (DeviceNode) o;
        return Objects.equals(macAddress, that.macAddress) && deviceType == that.deviceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(macAddress, deviceType);
    }
}
