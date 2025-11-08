package com.impactdryer.deviceapi.devicemanagment.domain;

public record MacAddress(String value) {

  // Regular expression to validate MAC address format
  public static final String MAC_ADDRESS_REGEX = "^([0-9A-Fa-f]{2}:){5}([0-9A-Fa-f]{2})$";

  public MacAddress {
    if (value == null || !value.matches(MAC_ADDRESS_REGEX)) {
      throw new InvalidMacAddressException("Invalid MAC address format");
    }
  }
}
