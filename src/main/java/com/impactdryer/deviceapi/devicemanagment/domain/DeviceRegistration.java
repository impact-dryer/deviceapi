package com.impactdryer.deviceapi.devicemanagment.domain;

public class DeviceRegistration {
  private final MacAddress deviceMacAddress;
  private final DeviceType deviceType;
  private final MacAddress uplinkMacAddress;

  public DeviceRegistration(MacAddress deviceMacAddress, DeviceType deviceType, MacAddress uplinkMacAddress) {
    this.deviceMacAddress = deviceMacAddress;
    this.deviceType = deviceType;
    this.uplinkMacAddress = uplinkMacAddress;
  }

  public DeviceRegistration(MacAddress deviceMacAddress, DeviceType deviceType) {
    this(deviceMacAddress, deviceType, null);
  }
}
